package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MetaDBConfig {
	
	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-meta")
	public DataSource metaDBSource() {
		
		return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost:3306/db1")
				.driverClassName("com.mysql.cj.jdbc.Driver")
				.username("root")
				.password("1234")
				.build();
	}
	
	@Primary
	@Bean
	public PlatformTransactionManager metaTransactionManager() {
		return new DataSourceTransactionManager(metaDBSource());
	}
}