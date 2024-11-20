package com.example.Boot_2_AdminServer.configuration;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpTraceActuatorConfiguration {

	@Bean
	public HttpExchangeRepository httpExchangeRepository() {

		return new InMemoryHttpExchangeRepository();

	}

}
