package com.neo.gatewayserver.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GatewayDbConfig {
	
	private final Environment environment;

	@Bean(name = "gatewayDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource gatewayDataSource() {
	    return DataSourceBuilder
	            .create()
	            .url(environment.getProperty("spring.datasource-data.url"))
	            .build();
	}
}
