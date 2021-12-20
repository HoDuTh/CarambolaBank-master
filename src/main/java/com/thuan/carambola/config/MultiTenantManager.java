package com.thuan.carambola.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "generalEntityManagerFactory",
        transactionManagerRef = "generalTransactionManager",
        basePackages = {"com.thuan.carambola.repositorygeneral"}
)
public class MultiTenantManager {

    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private final DataSourceProperties properties;
    public String port = "8091";
    private Function<String, DataSourceProperties> tenantResolver;

    private AbstractRoutingDataSource multiTenantDataSource;

    public MultiTenantManager(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "generalDataSource")
    public DataSource dataSource() {

        multiTenantDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentTenant.get();
            }
        };
        multiTenantDataSource.setTargetDataSources(tenantDataSources);
        if(Objects.equals(port, "8091"))
            multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
        else multiTenantDataSource.setDefaultTargetDataSource(secondDataSource());
        multiTenantDataSource.afterPropertiesSet();
        return multiTenantDataSource;
    }

    public void setTenantResolver(Function<String, DataSourceProperties> tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    public void setCurrentTenant(String tenantId) throws SQLException{
        if (tenantIsAbsent(tenantId)) {
            if (tenantResolver != null) {
                DataSourceProperties properties = null;
                try {
                    properties = tenantResolver.apply(tenantId);
                    log.debug("[d] Datasource properties resolved for tenant ID '{}'", tenantId);
                } catch (Exception e) {
                    log.debug("Tentant resolve error '{}'", tenantId);
                }

                String url = properties.getUrl();
                String username = properties.getUsername();
                String password = properties.getPassword();

                addTenant(tenantId, url, username, password);
            } else {
                log.debug("Tentant resolve error '{}'", tenantId);
            }
        }
        currentTenant.set(tenantId);
        log.debug("[d] Tenant '{}' set as current.", tenantId);
    }

    public void addTenant(String tenantId, String url, String username, String password) throws SQLException {

        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection c = dataSource.getConnection()) {
            tenantDataSources.put(tenantId, dataSource);
            multiTenantDataSource.afterPropertiesSet();
            log.debug("[d] Tenant '{}' added.", tenantId);
        }
    }

    public DataSource removeTenant(String tenantId) {
        Object removedDataSource = tenantDataSources.remove(tenantId);
        multiTenantDataSource.afterPropertiesSet();
        return (DataSource) removedDataSource;
    }

    public boolean tenantIsAbsent(String tenantId) {
        return !tenantDataSources.containsKey(tenantId);
    }

    public Collection<Object> getTenantList() {
        return tenantDataSources.keySet();
    }

    private DriverManagerDataSource defaultDataSource() {
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        defaultDataSource.setUrl("jdbc:sqlserver://DLI323\\SERVER1:8091;database=NGANHANG");
        defaultDataSource.setUsername("sa");
            defaultDataSource.setPassword("CSDLPT10");
        return defaultDataSource;
    }
    @Bean(name = "general2DataSource")
    public DataSource  secondDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceBuilder.url("jdbc:sqlserver://DLI323\\SERVER2:8092;database=NGANHANG");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("CSDLPT10");
        return dataSourceBuilder.build();
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
//    @Bean(name = "generalTransactionManager")
//    @Scope("prototype")
//    public JpaTransactionManager generalTransactionManager(
//            @Qualifier("generalEntityManagerFactory") EntityManagerFactory generalEntityManagerFactory) throws SQLException {
//        return new JpaTransactionManager(generalEntityManagerFactory);
//    }

//    @Bean(name = "generalTransactionManager")
//    @Scope("prototype")
//    public PlatformTransactionManager  generalTransactionManager(
//            @Qualifier("generalEntityManagerFactory") EntityManagerFactory generalEntityManagerFactory) throws SQLException {
//        return new JtaTransactionManager((javax.transaction.TransactionManager) generalEntityManagerFactory);
//    }
//    @Bean(name = "generalTransactionManager")
//    @Scope("prototype")
//    public PlatformTransactionManager generalTransactionManager(
//            @Qualifier("generalEntityManagerFactory") EntityManagerFactory generalEntityManagerFactory) throws SQLException {
//        return new JpaTransactionManager(generalEntityManagerFactory);
//    }

    @Bean(name = "generalTransactionManager")
    @Scope("prototype")
    public PlatformTransactionManager generalTransactionManager(
            @Qualifier("generalDataSource") DataSource generalDataSource) throws SQLException {
        return new DataSourceTransactionManager(generalDataSource);
    }
    @Bean(name = "general2TransactionManager")
    public PlatformTransactionManager general2TransactionManager(
            @Qualifier("generalDataSource") DataSource generalDataSource) throws SQLException {
        return new DataSourceTransactionManager(generalDataSource);
    }
}