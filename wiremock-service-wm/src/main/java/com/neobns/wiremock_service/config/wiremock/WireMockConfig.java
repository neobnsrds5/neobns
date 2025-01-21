package com.neobns.wiremock_service.config.wiremock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

@Configuration
public class WireMockConfig {

	@Bean
	public WireMockServer wireMockServer() {
		WireMockServer server = new WireMockServer(
	            WireMockConfiguration.wireMockConfig()
                .port(56789)
                .usingFilesUnderClasspath("wiremock")
        );
		
		 // 모든 요청에 대해 Fallback 처리
		server.stubFor(any(urlMatching(".*"))
				.willReturn(WireMock.aResponse()
		                .withStatus(503) // HTTP 503 상태 반환
		                .withHeader("Content-Type", "application/json")
		                .withBody("{ \"error\": \"Service Unavailable\", \"message\": \"Fallback response wiremock\" }")
	                ));


		
		//파비콘 No Content 처리
		server.stubFor(WireMock.request("GET", new UrlPattern(WireMock.equalTo("/favicon.ico"), false))
			    .willReturn(WireMock.aResponse()
			            .withStatus(204)
			            .withHeader("Content-Type", "text/plain")
			    	));
		
		server.addMockServiceRequestListener((request, response) -> {
		    // 요청 정보를 Stub으로 저장
		    saveStubMapping(
		        server,
		        request.getUrl(),
		        request.getMethod().getName(),
		        response.getStatus(),
		        response.getBodyAsString()
		    );
		});

		
		return server;
    }
	
	private void saveStubMapping(WireMockServer wireMockServer, String url, String method, int status, String body) {
		// 요청-응답 Stub 생성
		StubMapping stubMapping = WireMock.stubFor(WireMock.request(method, WireMock.urlEqualTo(url))
	        .willReturn(WireMock.aResponse()
	            .withStatus(status)
	            .withBody(body)));
		
		// Stub 저장
        wireMockServer.addStubMapping(stubMapping);
	
	}
	
	
}
