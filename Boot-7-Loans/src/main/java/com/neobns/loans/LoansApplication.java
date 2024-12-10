package com.neobns.loans;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
/*@ComponentScans({ @ComponentScan("com.neobns.loans.controller") })
@EnableJpaRepositories("com.neobns.loans.repository")
@EntityScan("com.neobns.loans.model")*/
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@OpenAPIDefinition(
		info = @Info(
				title = "Loans microservice REST API Documentation",
				description = "EazyBank Loans microservice REST API Documentation",
				version = "v1",
				license = @License(
						name = "Apache 2.0",
						url = "https://www.neobns.com"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "EazyBank Loans microservice REST API Documentation",
				url = "https://localhost:8090/swagger-ui/index.html"
		)
)
public class LoansApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoansApplication.class, args);
	}
}
