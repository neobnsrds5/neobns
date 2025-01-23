package neo.spider.sol.admin.batchServer.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TargetDbConfig {
	
	private final DBProperties dbProperties;
	
	@Bean(name = "targetDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-target")
	public DataSource targetDataSource() {
		return DataSourceBuilder.create().url(dbProperties.getTargetUrl()).build();
	}
}
