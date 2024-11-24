package com.example.neobns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.neobns.interceptor.RestfulLoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final RestfulLoggingInterceptor restfulLoggingInterceptor;

	public WebConfig(RestfulLoggingInterceptor restfulLoggingInterceptor) {
		this.restfulLoggingInterceptor = restfulLoggingInterceptor;

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(restfulLoggingInterceptor).addPathPatterns("/**"); // 모든 경로에 적용
//                .excludePathPatterns("/error", "/static/**"); // 제외 경로
	}
}
