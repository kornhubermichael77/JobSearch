package info.kornhuber.jobsearch.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantDataSourceProvider {

    @Value("${app.datasource.tenant.base-url}")
    private String baseUrl;

    @Value("${app.datasource.tenant.username}")
    private String username;

    @Value("${app.datasource.tenant.password}")
    private String password;

    private final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    public DataSource getDataSource(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new TenantResolutionException("Kein Tenant im TenantContext gesetzt");
        }

        return cache.computeIfAbsent(tenantId, this::createDataSource);
    }

    private DataSource createDataSource(String tenantId) {
        return DataSourceBuilder.create()
                .driverClassName("org.mariadb.jdbc.Driver")
                .url(baseUrl + tenantId)
                .username(username)
                .password(password)
                .build();
    }
}