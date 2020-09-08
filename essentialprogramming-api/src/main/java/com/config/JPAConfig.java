package com.config;


import com.api.env.resources.DishResources;
import com.util.cloud.ConfigurationManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.api.entities", "com.authentication.identityprovider.internal.entities"})
@EnableJpaRepositories({"com.api.repository", "com.authentication.identityprovider.internal.repository"})
public class JPAConfig {

    private final com.util.cloud.Configuration configuration = ConfigurationManager.getConfiguration();

    @Bean(name = "hikariDataSource")
    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(2));
        config.setMaxLifetime(1800000);
        config.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());

        config.setJdbcUrl(DishResources.DB_HOSTNAME.value());
        config.setUsername(DishResources.DB_USER.value());
        config.setPassword(DishResources.DB_PASSWORD.value());


        return new HikariDataSource(config);
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(hikariDataSource());
        factoryBean.setPackagesToScan("com.api.entities","com.authentication.identityprovider.internal.entities");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}