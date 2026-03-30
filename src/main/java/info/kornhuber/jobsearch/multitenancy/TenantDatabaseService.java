package info.kornhuber.jobsearch.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

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

    public void createTenantDatabase(String dbName) {
        createDatabaseIfNotExists(dbName);
        initializeSchemaIfNeeded(dbName);
    }

    public void initializeSchemaIfNeeded(String dbName) {
        DataSource tenantDataSource = buildTenantDataSource(dbName);

        try (Connection connection = tenantDataSource.getConnection()) {
            if (tableExists(connection, "company")) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Konnte Tabellenstatus für Tenant-DB nicht prüfen: " + dbName, e);
        }

        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("schema-tenant.sql"));
            populator.setContinueOnError(false);
            populator.setIgnoreFailedDrops(true);
            populator.execute(tenantDataSource);
        } catch (Exception e) {
            throw new RuntimeException("Konnte Tenant-Schema nicht initialisieren: " + dbName, e);
        }
    }

    public void dropTenantDatabase(String dbName) {
        DataSource adminDataSource = buildAdminDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(adminDataSource);

        try {
            jdbcTemplate.execute("DROP DATABASE IF EXISTS `" + dbName + "`");
        } catch (Exception e) {
            throw new RuntimeException("Konnte Tenant-Datenbank nicht löschen: " + dbName, e);
        }
    }

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

    private boolean tableExists(Connection connection, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();

            try (ResultSet rs = metaData.getTables(catalog, null, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Konnte nicht prüfen, ob Tabelle existiert: " + tableName, e);
        }
    }

    private DataSource buildAdminDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(baseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private DataSource buildTenantDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(buildTenantJdbcUrl(dbName));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private String buildTenantJdbcUrl(String dbName) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return normalizedBaseUrl + dbName;
    }
}