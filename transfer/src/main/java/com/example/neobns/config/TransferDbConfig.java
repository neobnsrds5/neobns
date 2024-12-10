package com.example.neobns.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.example.neobns.logging.common.MybatisLoggingInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TransferDbConfig {
	
	private final Environment environement;
	
	@Bean(name = "transferDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource transferDataSource() {
	    return DataSourceBuilder
	            .create()
	            .url(environement.getProperty("spring.datasource-data.url"))
	            .build();
	}

}
