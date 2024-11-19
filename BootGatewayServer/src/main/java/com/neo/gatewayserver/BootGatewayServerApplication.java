package com.neo.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class BootGatewayServerApplication {
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("transfer_app",
						r -> r.path("/transfer/**").filters(f -> f.stripPrefix(1)).uri("http://localhost:8082"))
				.route("main_app",
						r -> r.path("/main/**").filters(f -> f.stripPrefix(1)).uri("http://localhost:7777"))
				.build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BootGatewayServerApplication.class, args);
	}

}
