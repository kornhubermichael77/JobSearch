package info.kornhuber.jobsearch.multitenancy.config;

import info.kornhuber.jobsearch.multitenancy.TenantDataSourceProvider;
import info.kornhuber.jobsearch.multitenancy.TenantRoutingDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "info.kornhuber.jobsearch.domain.repository",
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantDataSourceConfig {

    @Bean
    public TenantRoutingDataSource tenantRoutingDataSource(
            TenantDataSourceProvider tenantDataSourceProvider
    ) {
        return new TenantRoutingDataSource(tenantDataSourceProvider);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("tenantRoutingDataSource") DataSource tenantRoutingDataSource
    ) {
        return builder
                .dataSource(tenantRoutingDataSource)
                .packages("info.kornhuber.jobsearch.domain.entity")
                .persistenceUnit("tenant")
                .build();
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}