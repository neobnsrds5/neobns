package com.neo.adminserver.configuration;

import javax.sql.DataSource;

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

@Configuration
@MapperScan(
    basePackages = "com.neo.adminserver.mapper.data", // 공통 Mapper 패키지
    sqlSessionFactoryRef = "dataSqlSessionFactory"
)
@RequiredArgsConstructor
public class DataDbConfig {
	
	private final Environment environment;

	@Bean(name = "dataDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDataSource() {
	    return DataSourceBuilder
	            .create()
	            .url(environment.getProperty("spring.datasource-data.url"))
	            .build();
	}
	
	@Bean(name = "dataSqlSessionFactory")
    public SqlSessionFactory dataSqlSessionFactory(
            @Qualifier("dataDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("com.neo.adminserver.dto"); // 공통 엔티티 경로
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mappers/data/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);

        return factoryBean.getObject();
    }

    @Bean(name = "dataSqlSessionTemplate")
    public SqlSessionTemplate dataSqlSessionTemplate(
            @Qualifier("dataSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
