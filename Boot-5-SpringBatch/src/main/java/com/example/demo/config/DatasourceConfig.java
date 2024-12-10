package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatasourceConfig {
	
	private final DBProperties properties;
	
	@Bean(name = "dataDBSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDatasource() {
		return DataSourceBuilder.create()
				.url(properties.getDataUrl()).build();
	}
	
	@Bean(name = "datasource")
	@ConfigurationProperties(prefix = "spring.datasource-meta")
	@Primary
	public DataSource metaDataSource() {
		return DataSourceBuilder.create()
				.url(properties.getMetaUrl()).build();
	}
	
	@Bean(name = "targetDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-target")
	public DataSource targetDataSource() {
		return DataSourceBuilder.create()
				.url(properties.getTargetUrl()).build();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(metaDataSource());
	}
}
