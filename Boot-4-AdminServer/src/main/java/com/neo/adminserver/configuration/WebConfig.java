package com.neo.adminserver.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	// TODO: 상세 설정 추가할 것
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//		.allowedOrigins("http://")
//		.allowedHeaders("*")
//		.allowedMethods("*")
//		.allowCredentials(true);
//	}

	public WebMvcConfigurer corsconConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4200","*")
						.allowedHeaders("*")
						.allowedMethods("*")
						.allowCredentials(true);
			}

		};

	}

}
