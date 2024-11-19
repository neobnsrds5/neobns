package com.neo.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class WeatherStoryApp1Application {

	public static void main(String[] args) {
		SpringApplication.run(WeatherStoryApp1Application.class, args);
	}

}
