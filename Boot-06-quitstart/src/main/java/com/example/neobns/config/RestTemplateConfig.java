package com.example.neobns.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.logging.common.RestTemplateLoggingInterceptor;

@Configuration
public class RestTemplateConfig {
	
	private final RestTemplateLoggingInterceptor restTemplateLoggingInterceptor;

    public RestTemplateConfig(RestTemplateLoggingInterceptor restTemplateLoggingInterceptor) {
        this.restTemplateLoggingInterceptor = restTemplateLoggingInterceptor;
    }
	
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.additionalInterceptors(restTemplateLoggingInterceptor).build();
    }

}
