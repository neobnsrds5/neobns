package com.neobns.wiremock_service.config.wiremock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;

public class DefaultStub {
	
	// 장애 상태 (503 Service Unavailable)
	public static void errorStub(WireMockServer server) {
		server.stubFor(any(urlPathMatching(".*/1$"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Service temporarily unavailable\"}")
                        ));
		
	}
	// 다운 상태 (Timeout 유발)
    public static void downStub(WireMockServer wireMockServer) {
        wireMockServer.stubFor(any(urlPathMatching(".*/2$"))
                .willReturn(aResponse()
                        .withFixedDelay(60000) // 60초 지연으로 Timeout 유발
                        )); 
    }
    // 지연 상태 (5초 후 응답 반환)
    public static void delayStub(WireMockServer wireMockServer) {
        wireMockServer.stubFor(any(urlPathMatching(".*/3$"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(5000) // 5초 지연
                        .withBody("{\"message\": \"Delayed response for /3\"}")
                        ));
    }
   
    
	
	
}
