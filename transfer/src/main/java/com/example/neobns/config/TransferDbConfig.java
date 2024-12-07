package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransferDbConfig {

<<<<<<< Updated upstream
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-target")
	public DataSource targetDataSource() {
=======
	@Bean(name = "dataDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDataSource() {
>>>>>>> Stashed changes
		return DataSourceBuilder.create().build();
	}
}
