package com.thuan.carambola.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "generalEntityManagerFactory",
        transactionManagerRef = "generalTransactionManager",
  basePackages = { "com.thuan.carambola.repositorygeneral" }
)
public class MultiTenantManager {

    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private final DataSourceProperties properties;
    private AbstractRoutingDataSource multiTenantDataSource;

    public MultiTenantManager(DataSourceProperties properties) {
        this.properties = properties;
    }
//    @Bean
//    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
//        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
//    }
    @Bean("generalDataSource")
    public DataSource dataSource() {
        multiTenantDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentTenant.get();
            }
        };
        multiTenantDataSource.setTargetDataSources(tenantDataSources);
        multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
        multiTenantDataSource.afterPropertiesSet();
        return multiTenantDataSource;
    }
    @Bean(name = "generalEntityManagerFactory")
	@Scope("prototype")
	public LocalContainerEntityManagerFactoryBean generalEntityManagerFactory(
			EntityManagerFactoryBuilder builder,
			@Qualifier("generalDataSource") DataSource generalDataSource) throws SQLException {
		Map<String, String> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.ddl-auto", "none");
		jpaProperties.put("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
		jpaProperties.put("show-sql", "true");
		jpaProperties.put("spring.jpa.properties.hibernate.dialect",
				"org.hibernate.dialect.SQLServer2012Dialect");
		return builder
				.dataSource(generalDataSource)
				.packages("com.thuan.carambola.entitygeneral")
				.persistenceUnit("general")
				.properties(jpaProperties)
				.build();
	}

	@Bean(name = "generalTransactionManager")
	@Scope("prototype")
	public PlatformTransactionManager generalTransactionManager(
			@Qualifier("generalEntityManagerFactory") EntityManagerFactory generalEntityManagerFactory) throws SQLException {
		return new JpaTransactionManager(generalEntityManagerFactory);
	}
    public void removeTenant(String tenantId)
    {
        if(tenantDataSources.isEmpty()||!tenantDataSources.containsKey(tenantId)) return;
        tenantDataSources.remove(tenantId);
    }
    public void addTenant(String tenantId, String url, String username, String password) throws SQLException {

        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection connection = dataSource.getConnection()) {
            tenantDataSources.put(tenantId, dataSource);
            multiTenantDataSource.afterPropertiesSet();
        }
    }

    public void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    private DriverManagerDataSource defaultDataSource() {
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        defaultDataSource.setUrl("jdbc:sqlserver://DLI323\\SERVER2:8092;database=NGANHANG");
        defaultDataSource.setUsername("sa");
        defaultDataSource.setPassword("CSDLPT10");
        return defaultDataSource;
    }
}