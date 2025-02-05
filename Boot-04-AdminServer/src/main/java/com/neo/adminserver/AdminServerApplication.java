package com.neo.adminserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAdminServer
@ComponentScan(basePackages = {"com.neo.adminserver", "com.neobns.admin"}) // 패키지가 분리되었으므로 컴포넌트를 스캔할 패키지를 직접 명시
public class AdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServerApplication.class, args);
	}

}
