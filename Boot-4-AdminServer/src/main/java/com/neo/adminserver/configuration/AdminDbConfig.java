package com.neo.adminserver.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminDbConfig {

	@Bean(name = "dataDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDataSource() {
		return DataSourceBuilder.create().build();
	}
}
