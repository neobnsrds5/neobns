package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "values")
@Getter
@Setter
public class RabbitProp {
	private String exchange;
	private String routingKey;
	private String queue;
}
