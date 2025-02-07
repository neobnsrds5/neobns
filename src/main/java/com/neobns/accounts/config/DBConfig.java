package com.neobns.accounts.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
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

import com.example.logging.JPALoggingInspector;
import com.example.logging.MyBatisInterceptor;
import com.neobns.accounts.mybatis.ProjectMyBatisInterceptor;
import com.neobns.accounts.properties.DBProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableJpaRepositories(
		basePackages = "com.neobns.accounts.repository"
		,entityManagerFactoryRef = "dataEntityManager"
		, transactionManagerRef = "dataTransactionsManager")
@RequiredArgsConstructor
public class DBConfig {
	private final DBProperties dbProperties;
	private final JPALoggingInspector inspector;
	
	@Bean(name = "dataDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDataSource() {    	
		return DataSourceBuilder.create()
				.url(dbProperties.getDataUrl())
				.build();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		
		// Mybatis interceptor 등록
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//		configuration.addInterceptor(new MybatisLoggingInterceptor());
		
		configuration.addInterceptor(new MyBatisInterceptor());
		
		Interceptor[] interceptors = new Interceptor[] {
				new ProjectMyBatisInterceptor(),
				new MyBatisInterceptor()
		};
		
		factory.setDataSource(dataDataSource());
		factory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:mappers/*.xml"));
		
//		factory.setConfiguration(configuration); // Mybatis interceptor 등록
		
		factory.addPlugins(interceptors);
		
		return factory.getObject();
		
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setDataSource(dataDataSource());
		em.setPackagesToScan(new String[] {"com.neobns.accounts.entity"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
		properties.put("hibernate.session_factory.statement_inspector", inspector);
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
