package info.kornhuber.jobsearch.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final TenantDataSourceProvider tenantDataSourceProvider;

    public TenantRoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;

        DataSource defaultDataSource = tenantDataSourceProvider.getDataSource("tenant_default");

        Map<Object, Object> initialTargets = new HashMap<>();
        initialTargets.put("tenant_default", defaultDataSource);

        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(initialTargets);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenant();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = TenantContext.getTenant();
        return tenantDataSourceProvider.getDataSource(tenantId);
    }
}