package com.example.neobns.config;

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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.neobns.properties.DBProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@MapperScan(basePackages = "com.example.neobns.oraclemapper", sqlSessionFactoryRef = "oracleSqlSessionFactory")
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
	
	@Bean(name = "oracleSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("spiderDataSource") DataSource datasource) throws Exception{
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(datasource);
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:oraclemappers/*.xml"));
		return sessionFactory.getObject();
	}
	
	@Bean(name = "oracleTemplate")
	public SqlSessionTemplate oracleTemplate(SqlSessionFactory oracleSqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(oracleSqlSessionFactory);
	}

}
