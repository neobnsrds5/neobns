package com.neo.gatewayserver.filter;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

	public GlobalFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			final String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");
			String generatedRequestId = (requestId == null || requestId.isEmpty()) ? UUID.randomUUID().toString()
					: requestId;
			
			// UserId
			final String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
			String generatedUserId = (userId == null || userId.isEmpty()) ?  generateRandomUserId() : userId;

			

			// Request ID를 MDC에 설정하여 로깅에 포함
			MDC.put("requestId", generatedRequestId);
			MDC.put("userId", generatedUserId);

			// Request Header에 Request ID, User ID 추가
			ServerHttpRequest updatedRequest = exchange.getRequest().mutate().header("X-Request-ID", generatedRequestId).header("X-User-ID", generatedUserId).build();
			ServerWebExchange updatedExchange = exchange.mutate().request(updatedRequest).build();

			// 로그 출력: 게이트웨이에서 요청 시작
			logRequestDetails(updatedRequest);

			return chain.filter(updatedExchange).then(Mono.fromRunnable(() -> {
	        	String responseRequestId = updatedExchange.getResponse().getHeaders().getFirst("X-Request-ID");
	        	String responseUserId = updatedExchange.getResponse().getHeaders().getFirst("X-User-ID");
	        	if(responseRequestId == null || responseRequestId.isEmpty()) {
	        		responseRequestId = generatedRequestId;
	        	}
	        	if(responseUserId == null || responseUserId.isEmpty()) {
	        		responseUserId = generatedUserId;
	        	}
	        	
	        	logResponseDetails(updatedExchange, responseRequestId, responseUserId);
	        	
	        	MDC.clear();
	        }));
			
		};
	}

	private String generateRandomUserId() {
		// 고유 User ID 생성 로직
		return "user-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private void logRequestDetails(ServerHttpRequest request) {
		String requestId = MDC.get("requestId");
		String userId = MDC.get("userId");
		String uri = request.getURI().toString();
		System.out.println(String.format("[%s][%s] Gateway Request: %s", requestId, userId, uri));
	}

	private void logResponseDetails(ServerWebExchange exchange, String requestId, String userId) {
		System.out.println(String.format("[%s][%s] Gateway Response: %s", requestId, userId,
				exchange.getResponse().getStatusCode()));
	}

	public static class Config {
		// 필요 시 사용자 정의 설정 추가
	}

}