package com.neobns.wiremock_service.config.wiremock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Configuration
public class WireMockConfig {

	@Bean
	public WireMockServer wireMockServer() {
		WireMockServer server = new WireMockServer(
	            WireMockConfiguration.wireMockConfig()
                .port(56789)
                .usingFilesUnderClasspath("wiremock")
                .extensions(new TransformStub())
        );
		
		server.start();

		//파비콘 No Content 처리
		server.stubFor(WireMock.request("GET", new UrlPattern(WireMock.equalTo("/favicon.ico"), false))
			    .willReturn(WireMock.aResponse()
			            .withStatus(204)
			            .withHeader("Content-Type", "text/plain")
			    	));
		
		server.stubFor(any(urlPathMatching("/register"))
                .willReturn(aResponse()
                        .withTransformers("trans") // TransformStub에서 등록된 이름 사용
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody("{\"message\": \"Register endpoint ready!\"}")));
		
		
		DefaultStub.errorStub(server);
		DefaultStub.delayStub(server);
		DefaultStub.downStub(server);
				
		return server;
    }
	

	
	
}
