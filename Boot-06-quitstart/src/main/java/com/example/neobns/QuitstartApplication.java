package com.example.neobns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
public class QuitstartApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuitstartApplication.class, args);
	}

}
