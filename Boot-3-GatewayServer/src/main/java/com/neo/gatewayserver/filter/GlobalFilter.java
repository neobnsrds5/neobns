package com.neo.gatewayserver.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
	
	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
	private static final long SLOW_PAGE_THRESHOLD_MS = 10; // slow page 기준, 나중에 환경 변수로 빼기..!

	public GlobalFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			
			// RequestId
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
			long startTime = System.currentTimeMillis();
			logRequestDetails(updatedRequest);

			return chain.filter(updatedExchange).then(Mono.fromRunnable(() -> {
				Long elapsedTime = System.currentTimeMillis() - startTime;
				
	        	String responseRequestId = updatedExchange.getResponse().getHeaders().getFirst("X-Request-ID");
	        	String responseUserId = updatedExchange.getResponse().getHeaders().getFirst("X-User-ID");
	        	
	        	if(responseRequestId == null || responseRequestId.isEmpty()) {
	        		responseRequestId = generatedRequestId;
	        	}
	        	if(responseUserId == null || responseUserId.isEmpty()) {
	        		responseUserId = generatedUserId;
	        	}
	        	
	        	logResponseDetails(updatedExchange, responseRequestId, responseUserId, elapsedTime);
	        	
//	        	MDC.clear();
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
		
		MDC.put("className", "Gateway Request");
		MDC.put("methodName", uri);
		
		traceLogger.info("[{}][{}] Gateway Request: {}", requestId, userId, uri);
		
		MDC.remove("className");
        MDC.remove("methodName");
	}

	private void logResponseDetails(ServerWebExchange exchange, String requestId, String userId, long elapsedTime) {
		String statusCode = exchange.getResponse().getStatusCode().toString();
		String uri = exchange.getRequest().getURI().toString();
		
		MDC.put("requestId", requestId);
		MDC.put("userId", userId);
		MDC.put("className", "Gateway Response");
		MDC.put("methodName", uri);
		MDC.put("executeTime", Long.toString(elapsedTime));
		
		traceLogger.info("[{}][{}] Gateway Response: {} {}ms", requestId, userId, statusCode, elapsedTime);
		// 설정 시간보다 느리면 slow 로깅
        if(elapsedTime > SLOW_PAGE_THRESHOLD_MS) {
        	slowLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), MDC.get("executeTime"));
        }
		
		MDC.clear();
	}

	public static class Config {
		// 필요 시 사용자 정의 설정 추가
	}

}