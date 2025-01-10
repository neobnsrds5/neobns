package com.neo.gatewayserver.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DBProperties {
	
	private final Environment environement;
	
	public String getMetaUrl() {
		return environement.getProperty("spring.datasource-meta.url");
	}
	
	public String getDataUrl() {
		return environement.getProperty("spring.datasource-data.url");
	}
	
	public String getTargetUrl() {
		return environement.getProperty("spring.datasource-target.url");
	}
	
	public String getSpiderUrl() {
		return environement.getProperty("spring.datasource-spiderdb.url");
	}
}
