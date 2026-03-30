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
        String effectiveTenant = (tenantId == null || tenantId.isBlank())
                ? "tenant_default"
                : tenantId;

        return cache.computeIfAbsent(effectiveTenant, this::createDataSource);
    }

    private DataSource createDataSource(String tenantId) {
        String dbName = tenantId.equals("tenant_default") ? "jobsearch" : tenantId;

        return DataSourceBuilder.create()
                .driverClassName("org.mariadb.jdbc.Driver")
                .url(baseUrl + dbName)
                .username(username)
                .password(password)
                .build();
    }
}