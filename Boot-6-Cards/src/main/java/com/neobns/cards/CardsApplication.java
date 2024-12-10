package com.neobns.cards;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
/*@ComponentScans({ @ComponentScan("com.neobns.cards.controller") })
@EnableJpaRepositories("com.neobns.cards.repository")
@EntityScan("com.neobns.cards.model")*/
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
		info = @Info(
				title = "Cards microservice REST API Documentation",
				description = "Neobns Cards microservice REST API Documentation",
				version = "v1",
				license = @License(
						name = "Apache 2.0",
						url = "https://www.neobns.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Neobns Cards microservice REST API Documentation",
				url = "http://localhost:9000/swagger-ui/index.html"
		)
)
public class CardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApplication.class, args);
	}
}
