package neo.spider.solution.batch.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MetaDbConfig {
	
	private final DBProperties properties;
	
	@Primary
	@Bean(name = "metaDataSource")
	@ConfigurationProperties(prefix = "spider.batch.datasource-meta")
	public DataSource metaDataSource() {
		return DataSourceBuilder.create().url(properties.getMetaUrl()).build();
	}
	
	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager metaTransactionManager() {
		return new DataSourceTransactionManager(metaDataSource());
	}
}