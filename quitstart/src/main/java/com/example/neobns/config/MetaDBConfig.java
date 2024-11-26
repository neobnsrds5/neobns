package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.neobns.properties.DBProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MetaDBConfig {
	
	private final DBProperties properties;
	
	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource-meta")
	public DataSource metaDBSource() {
		
		return DataSourceBuilder.create()
				.url(properties.getMetaUrl())
				.build();
	}
	
	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager metaTransactionManager() {
		return new DataSourceTransactionManager(metaDBSource());
	}
}