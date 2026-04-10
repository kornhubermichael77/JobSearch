package info.kornhuber.jobsearch.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Verantwortlich für das technische Provisionieren von Tenant-Datenbanken.
 *
 * Diese Klasse:
 * - legt neue Tenant-Datenbanken an
 * - löscht Tenant-Datenbanken
 * - stößt nach dem Erstellen oder bei Bedarf auch für bestehende DBs die Schema-Migration an
 *
 * Wichtig:
 * Das eigentliche Schema wird ausschließlich über Flyway-Migrationen verwaltet.
 */
@Service
public class TenantDatabaseService {

    @Value("${app.datasource.tenant.base-url}")
    private String baseUrl;

    @Value("${app.datasource.tenant.username}")
    private String username;

    @Value("${app.datasource.tenant.password}")
    private String password;

    @Value("${app.datasource.tenant.driver-class-name}")
    private String driverClassName;

    private final TenantMigrationService tenantMigrationService;

    public TenantDatabaseService(TenantMigrationService tenantMigrationService) {
        this.tenantMigrationService = tenantMigrationService;
    }

    /**
     * Erstellt eine Tenant-Datenbank, falls sie noch nicht existiert,
     * und migriert sie anschließend auf den aktuellen Schema-Stand.
     *
     * Diese Methode ist für NEUE Tenants gedacht.
     *
     * @param dbName technischer Name der Tenant-Datenbank
     */
    public void createTenantDatabase(String dbName) {
        createDatabaseIfNotExists(dbName);

        DataSource tenantDataSource = buildTenantDataSource(dbName);

        tenantMigrationService.migrateTenantDatabase(tenantDataSource);
    }

    /**
     * Migriert eine bereits existierende Tenant-Datenbank auf den aktuellen Schema-Stand.
     *
     * Diese Methode ist wichtig für bestehende Tenants, deren Datenbank bereits existiert,
     * aber möglicherweise noch nicht auf dem neuesten Flyway-Stand ist.
     *
     * Es wird bewusst KEINE Datenbank neu erzeugt.
     *
     * @param dbName technischer Name der bestehenden Tenant-Datenbank
     */
    public void migrateExistingTenantDatabase(String dbName) {
        DataSource tenantDataSource = buildTenantDataSource(dbName);

        tenantMigrationService.migrateTenantDatabase(tenantDataSource);
    }

    /**
     * Löscht eine Tenant-Datenbank vollständig.
     *
     * @param dbName Name der zu löschenden Tenant-Datenbank
     */
    public void dropTenantDatabase(String dbName) {
        DataSource adminDataSource = buildAdminDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(adminDataSource);

        try {
            jdbcTemplate.execute("DROP DATABASE IF EXISTS `" + dbName + "`");
        } catch (Exception e) {
            throw new RuntimeException("Konnte Tenant-Datenbank nicht löschen: " + dbName, e);
        }
    }

    /**
     * Legt die Datenbank technisch an, falls sie noch nicht existiert.
     *
     * @param dbName Name der Tenant-Datenbank
     */
    private void createDatabaseIfNotExists(String dbName) {
        DataSource adminDataSource = buildAdminDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(adminDataSource);

        try {
            jdbcTemplate.execute(
                    "CREATE DATABASE IF NOT EXISTS `" + dbName + "` " +
                            "CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci"
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Konnte Tenant-Datenbank nicht anlegen: " + dbName +
                            ". Prüfe DB-Rechte und base-url: " + baseUrl,
                    e
            );
        }
    }

    /**
     * Baut eine DataSource auf Server-/Admin-Ebene.
     * Diese wird benötigt, um CREATE DATABASE / DROP DATABASE auszuführen.
     */
    private DataSource buildAdminDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(baseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Baut eine DataSource für eine konkrete Tenant-Datenbank.
     *
     * @param dbName Name der Tenant-Datenbank
     */
    private DataSource buildTenantDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(buildTenantJdbcUrl(dbName));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Erzeugt die JDBC-URL für eine konkrete Tenant-Datenbank.
     *
     * Beispiel:
     * baseUrl = jdbc:mariadb://localhost:3306/
     * dbName  = tenant_abc123
     * Ergebnis:
     * jdbc:mariadb://localhost:3306/tenant_abc123
     */
    private String buildTenantJdbcUrl(String dbName) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return normalizedBaseUrl + dbName;
    }
}