package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TargetDataSourceConfig {
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-target")
	public DataSource targetDataSource() {
		return DataSourceBuilder.create().url("jdbc:mysql://localhost:3306/db3").build();
	}
}
