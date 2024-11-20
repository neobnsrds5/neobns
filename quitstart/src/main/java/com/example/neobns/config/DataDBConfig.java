package com.example.neobns.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
		basePackages = "com.example.neobns.repository"
		,entityManagerFactoryRef = "dataEntityManager"
		, transactionManagerRef = "dataTransactionsManager")
public class DataDBConfig {
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDBSource() {
		return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost:3307/db2")
				.driverClassName("com.mysql.cj.jdbc.Driver")
				.username("root")
				.password("1234")
				.build();
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setDataSource(dataDBSource());
		em.setPackagesToScan(new String[] {"com.example.neobns.entity"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		em.setJpaPropertyMap(properties);
		
		return em;
	}
	
	@Bean
	public PlatformTransactionManager dataTransactionsManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(dataEntityManager().getObject());
		return transactionManager;
	}
	
}
