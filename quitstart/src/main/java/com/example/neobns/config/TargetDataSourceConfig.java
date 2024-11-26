package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.neobns.properties.DBProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TargetDataSourceConfig {
	
	private final DBProperties dbProperties;
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-target")
	public DataSource targetDataSource() {
		return DataSourceBuilder.create().url(dbProperties.getTargetUrl()).build();
	}
}
