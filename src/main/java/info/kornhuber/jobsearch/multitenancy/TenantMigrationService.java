package info.kornhuber.jobsearch.multitenancy;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Verantwortlich für das Anwenden der versionierten Flyway-Migrationen
 * auf eine konkrete Tenant-Datenbank.
 *
 * Warum eine eigene Klasse?
 * - klare Verantwortung
 * - TenantDatabaseService kümmert sich nur noch um "DB anlegen/löschen"
 * - Migration ist ein eigener Schritt und dadurch besser testbar und verständlicher
 */
@Service
public class TenantMigrationService {

    /**
     * Führt alle noch offenen Flyway-Migrationen für die übergebene Tenant-DB aus.
     *
     * Beispiel:
     * - V1__init_tenant.sql
     * - später V2__add_address_owner_user_id.sql
     * - später V3__rename_sidemarks_to_notes.sql
     *
     * Flyway merkt sich über seine eigene Historientabelle,
     * welche Migrationen bereits ausgeführt wurden.
     *
     * @param tenantDataSource DataSource der Ziel-Tenant-Datenbank
     */
    public void migrateTenantDatabase(DataSource tenantDataSource) {
        Flyway flyway = Flyway.configure()
                // Die konkrete Tenant-Datenbank, die migriert werden soll
                .dataSource(tenantDataSource)

                // Hier liegen die Tenant-Migrationsdateien im resources-Ordner
                .locations("classpath:db/migration/tenant")

                // Sinnvoll, falls eine DB bereits existiert und Flyway-Historie noch nicht vorhanden ist.
                // Für komplett neue DBs ist das nicht kritisch, aber unproblematisch.
                .baselineOnMigrate(true)

                .load();

        flyway.migrate();
    }
}