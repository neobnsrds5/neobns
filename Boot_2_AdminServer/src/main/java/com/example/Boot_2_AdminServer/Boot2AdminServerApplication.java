package com.example.Boot_2_AdminServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class Boot2AdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Boot2AdminServerApplication.class, args);
	}

}
