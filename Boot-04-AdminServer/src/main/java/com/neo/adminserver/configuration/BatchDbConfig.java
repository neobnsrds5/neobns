package com.neo.adminserver.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@Configuration
@MapperScan(
    basePackages = "com.neo.adminserver.mapper.db1", // 공통 Mapper 패키지
    sqlSessionFactoryRef = "batchSqlSessionFactory"
)
@RequiredArgsConstructor
public class BatchDbConfig {
	
	private final Environment environment;

    @Bean(name = "batchDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-meta") // batch DB 관련 설정
    public DataSource batchDataSource() {
        return DataSourceBuilder.create().url(environment.getProperty("spring.datasource-meta.url")).build();
    }

    @Bean(name = "batchSqlSessionFactory")
    public SqlSessionFactory batchSqlSessionFactory(
            @Qualifier("batchDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("com.neo.adminserver.dto"); // 공통 엔티티 경로
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mappers/db1/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);
        return factoryBean.getObject();
    }

    @Bean(name = "batchSqlSessionTemplate")
    public SqlSessionTemplate batchSqlSessionTemplate(
            @Qualifier("batchSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
