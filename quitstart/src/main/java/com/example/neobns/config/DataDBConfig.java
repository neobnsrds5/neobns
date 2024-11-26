package com.example.neobns.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.neobns.logging.common.MybatisLoggingInterceptor;
import com.example.neobns.properties.DBProperties;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableJpaRepositories(
		basePackages = "com.example.neobns.repository"
		,entityManagerFactoryRef = "dataEntityManager"
		, transactionManagerRef = "dataTransactionsManager")
@RequiredArgsConstructor
public class DataDBConfig {
	
	private final DBProperties dbProperties;
	
	@Bean(name = "dataDBSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDBSource() {
		return DataSourceBuilder.create()
				.url(dbProperties.getDataUrl())
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
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		
		// Mybatis interceptor 등록
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.addInterceptor(new MybatisLoggingInterceptor());
		
		factory.setDataSource(dataDBSource());
		factory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:mappers/*.xml"));
		factory.setConfiguration(configuration); // Mybatis interceptor 등록
		
		return factory.getObject();
		
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}
	
}
