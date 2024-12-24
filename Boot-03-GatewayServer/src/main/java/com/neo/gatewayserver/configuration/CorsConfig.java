package com.neo.gatewayserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsWebFilter corsWebFilter() {
		
		return new CorsWebFilter(configurationSource());
		
	}
	
	@Bean
	CorsConfigurationSource configurationSource() {
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
		config.addAllowedMethod(HttpMethod.GET);
		config.addAllowedMethod(HttpMethod.DELETE);
		source.registerCorsConfiguration("/**", config);
		return source;
		
	}
	
	

}
