package neo.spider.solution.batch.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DBProperties {
	
	private final Environment environment;
	
	public String getMetaUrl() {
		return environment.getProperty("spider.batch.datasource-meta.url");
	}
	
	public String getDataUrl() {
		return environment.getProperty("spider.batch.datasource-data.url");
	}
	
	public String getTargetUrl() {
		return environment.getProperty("spider.batch.datasource-target.url");
	}
}
