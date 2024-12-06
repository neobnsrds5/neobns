package com.demo.jgen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class BatchDataConfig {


    @Bean(name = "dataDBSource")
    @ConfigurationProperties(prefix = "spring.datasource-data")
    public DataSource dataDBSource(){
        return null;
    }
}
