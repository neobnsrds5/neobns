package com.example.neobns.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.neobns.properties.DBProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpiderDbConfig {

	private final DBProperties dbProperties;

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource-spiderdb")
	public DataSource spiderDataSource() {
		return DataSourceBuilder.create().url(dbProperties.getSpiderUrl()).build();
	}

	@Bean
	public JdbcTemplate spiderTemplate() {
		return new JdbcTemplate(spiderDataSource());

	}

}
