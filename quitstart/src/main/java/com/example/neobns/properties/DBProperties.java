package com.example.neobns.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DBProperties {
	
	private Environment environement;
	
	public DBProperties(Environment environment) {
		this.environement = environment;
	}
	
	public String getMetaUrl() {
		return environement.getProperty("spring.datasource-meta.url");
	}
	
	public String getDataUrl() {
		return environement.getProperty("spring.datasource-data.url");
	}
	
	public String getTargetUrl() {
		return environement.getProperty("spring.datasource-target.url");
	}
}
