package info.kornhuber.jobsearch.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final TenantDataSourceProvider tenantDataSourceProvider;

    public TenantRoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getTenant();

        if (tenant == null || tenant.isBlank()) {
            throw new TenantResolutionException("Kein Tenant für den aktuellen Request verfügbar");
        }

        return tenant;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = TenantContext.getTenant();

        if (tenantId == null || tenantId.isBlank()) {
            throw new TenantResolutionException("Kein Tenant für den aktuellen Request verfügbar");
        }

        return tenantDataSourceProvider.getDataSource(tenantId);
    }
}