package com.neobns.wiremock_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WiremockServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WiremockServiceApplication.class, args);
	}

}