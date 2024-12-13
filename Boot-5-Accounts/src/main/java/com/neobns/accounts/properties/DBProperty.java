package com.neobns.accounts.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DBProperty {

	private final Environment environment;
	
	public String getMetaUrl() {
		return environment.getProperty("spring.datasource-meta.url");
	}
	
	public String getDataUrl() {
		return environment.getProperty("spring.datasource-data.url");
	}
	
	public String getTargetUrl() {
		return environment.getProperty("spring.datasource-target.url");
	}
}
