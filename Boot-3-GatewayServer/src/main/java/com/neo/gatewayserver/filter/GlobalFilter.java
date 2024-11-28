package com.neo.gatewayserver.filter;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

	public GlobalFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			String requestId = request.getHeaders().getFirst("X-Request-ID");

			if (requestId == null || requestId.isEmpty()) {
				requestId = UUID.randomUUID().toString(); // 고유 Request ID 생성
			}
			
			// UserId 
			 String userId = request.getHeaders().getFirst("X-User-ID");
	            if (userId == null || userId.isEmpty()) {
	                userId = generateRandomUserId(); // 랜덤 유저 ID 생성
	            }

			// Request ID를 MDC에 설정하여 로깅에 포함
			MDC.put("requestId", requestId);
			MDC.put("userId", userId);

			// Request Header에 Request ID, User ID 추가
			 ServerHttpRequest updatedRequest = request.mutate()
		                .header("X-Request-ID", requestId)
		                .header("X-User-ID", userId)
		                .build();

			ServerWebExchange updatedExchange = exchange.mutate().request(updatedRequest).build();

			// 로그 출력: 게이트웨이에서 요청 시작
			logRequestDetails(updatedRequest);

			return chain.filter(updatedExchange).doFinally(signalType -> {
				// 로그 출력: 게이트웨이에서 요청 완료
				logResponseDetails(updatedExchange);
				MDC.clear();
			});
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

    private void logResponseDetails(ServerWebExchange exchange) {
        String requestId = MDC.get("requestId");
        String userId = MDC.get("userId");
        System.out.println(String.format("[%s][%s] Gateway Response: %s", 
            requestId, userId, exchange.getResponse().getStatusCode()));
    }


	public static class Config {
		// 필요 시 사용자 정의 설정 추가
	}

}