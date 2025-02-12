package neo.spider.admin.batch.config;

import lombok.RequiredArgsConstructor;
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

import javax.sql.DataSource;

@Configuration
@MapperScan(
    basePackages = "neo.spider.admin.batch.mapper",
    sqlSessionFactoryRef = "metaSqlSessionFactory"
)
@RequiredArgsConstructor
public class MetaDbConfig {
	
	private final Environment environment;

    @Bean(name = "metaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-meta") // batch DB 관련 설정
    public DataSource metaDataSource() {
        return DataSourceBuilder.create().url(environment.getProperty("spring.datasource-meta.url")).build();
    }

    @Bean(name = "metaSqlSessionFactory")
    public SqlSessionFactory metaSqlSessionFactory(
            @Qualifier("metaDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("neo.spider.admin.batch.dto");
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mappers/meta/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);
        return factoryBean.getObject();
    }

    @Bean(name = "metaSqlSessionTemplate")
    public SqlSessionTemplate metaSqlSessionTemplate(
            @Qualifier("metaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
