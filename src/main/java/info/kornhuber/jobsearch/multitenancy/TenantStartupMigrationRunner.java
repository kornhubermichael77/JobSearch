package info.kornhuber.jobsearch.multitenancy;

import info.kornhuber.jobsearch.auth.entity.UserEntity;
import info.kornhuber.jobsearch.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Migriert beim Start der Anwendung alle bereits bekannten Tenant-Datenbanken.
 *
 * Warum ist das sinnvoll?
 * - bestehende Tenant-DBs werden automatisch auf den neuesten Schema-Stand gebracht
 * - Änderungen wie neue Spalten oder Indizes müssen nicht manuell pro Tenant eingespielt werden
 * - Login/Requests bleiben frei von Migrationslogik
 *
 * Datenquelle:
 * Die bekannten Tenant-Datenbanken werden aus der Auth-DB gelesen,
 * konkret aus dem Feld tenant_db_name der Benutzer.
 */
@Configuration
public class TenantStartupMigrationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantStartupMigrationRunner.class);

    @Bean
    public ApplicationRunner migrateExistingTenantsOnStartup(
            UserRepository userRepository,
            TenantDatabaseService tenantDatabaseService
    ) {
        return args -> {
            /**
             * LinkedHashSet:
             * - vermeidet doppelte Tenant-Namen
             * - behält die Einfügereihenfolge stabil
             */
            Set<String> tenantDbNames = new LinkedHashSet<>();

            for (UserEntity user : userRepository.findAll()) {
                if (user.getTenantDbName() != null && !user.getTenantDbName().isBlank()) {
                    tenantDbNames.add(user.getTenantDbName());
                }
            }

            if (tenantDbNames.isEmpty()) {
                log.info("Keine bestehenden Tenant-Datenbanken für Startup-Migration gefunden.");
                return;
            }

            log.info("Starte Flyway-Migration für {} bestehende Tenant-Datenbanken.", tenantDbNames.size());

            for (String tenantDbName : tenantDbNames) {
                log.info("Migriere bestehende Tenant-Datenbank: {}", tenantDbName);

                try {
                    tenantDatabaseService.migrateExistingTenantDatabase(tenantDbName);
                } catch (Exception ex) {
                    /**
                     * Hier bewusst hart abbrechen:
                     * Wenn eine Tenant-DB nicht migriert werden kann,
                     * soll die Anwendung nicht in einem teilweise inkonsistenten Zustand starten.
                     */
                    throw new IllegalStateException(
                            "Migration der bestehenden Tenant-Datenbank fehlgeschlagen: " + tenantDbName,
                            ex
                    );
                }
            }

            log.info("Startup-Migration aller bekannten Tenant-Datenbanken abgeschlossen.");
        };
    }
}