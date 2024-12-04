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
	
	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final String USER_ID_HEADER = "X-User-ID";
	private static final String CLIENT_IP_HEADER = "X-Forwarded-For";
	private static final String USER_AGENT_HEADER = "User-Agent";
	
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	private static final String MDC_USER_ID_KEY = "userId";
	private static final String MDC_USER_AGENT = "userAgent";
	private static final String MDC_CLIENT_IP = "clientIp";
	
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
			String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
			if(requestId == null || requestId.isEmpty()) {
				requestId = UUID.randomUUID().toString();
			}
			
			// UserId
			String userId = exchange.getRequest().getHeaders().getFirst(USER_ID_HEADER);
			if (userId == null || userId.isEmpty()) {
				userId = generateRandomUserId();
			}
			
			// ClientIp
			// 로드 밸런서 환경에서 헤더가 비어 있을 수 있으므로 getRemoteAddress 메소드 사용
			String clientIp = exchange.getRequest().getHeaders().getFirst(CLIENT_IP_HEADER);
			if(clientIp == null || clientIp.isEmpty()) {
				System.out.println("클라이언트 아이피가 없음!!");
				clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
				
				if(clientIp == null || clientIp.isEmpty()) {
					clientIp = "UNKNOWN";
				}
			}
			
			// UserAgent
			String userAgent = exchange.getRequest().getHeaders().getFirst(USER_AGENT_HEADER);
			if (userAgent == null) {
	        	userAgent = "UNKNOWN";
	        } else if (userAgent.toLowerCase().contains("mobi")) {
	        	userAgent = "Mobile";
	        } else {
	        	userAgent = "PC";
	        }

			// MDC에 설정하여 로깅에 포함
			MDC.put(MDC_REQUEST_ID_KEY, requestId);
	        MDC.put(MDC_USER_ID_KEY, userId);
	        MDC.put(MDC_CLIENT_IP, clientIp);
	        MDC.put(MDC_USER_AGENT, userAgent);

			// Request Header에 데이터 추가
			ServerHttpRequest updatedRequest = exchange.getRequest().mutate().header(REQUEST_ID_HEADER, requestId).header(USER_ID_HEADER, userId).build();
			ServerWebExchange updatedExchange = exchange.mutate().request(updatedRequest).build();

			// 로그 출력: 게이트웨이에서 요청 시작
			long startTime = System.currentTimeMillis();
			logRequestDetails(updatedRequest);

			return chain.filter(updatedExchange).then(Mono.fromRunnable(() -> {
				Long elapsedTime = System.currentTimeMillis() - startTime;
	        	
	        	logResponseDetails(updatedExchange, elapsedTime);
	        	
	        }));
			
		};
	}

	private String generateRandomUserId() {
		// 고유 User ID 생성 로직
		return "user-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private void logRequestDetails(ServerHttpRequest request) {
		
		String uri = request.getURI().toString();
		String method = request.getMethod().toString();
		
		MDC.put("className", uri);
		MDC.put("methodName", method);
		
		traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), uri, method, "start");
		
		MDC.remove("className");
        MDC.remove("methodName");
	}

	private void logResponseDetails(ServerWebExchange exchange, long elapsedTime) {
		
		String statusCode = exchange.getResponse().getStatusCode().toString();
		String requestId = exchange.getResponse().getHeaders().getFirst("X-Request-ID");
		String userId = exchange.getResponse().getHeaders().getFirst("X-User-ID");
		String uri = exchange.getRequest().getURI().toString();
		String method = exchange.getRequest().getMethod().toString();

		// ClientIp
		// 로드 밸런서 환경에서 헤더가 비어 있을 수 있으므로 getRemoteAddress 메소드 사용
		String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
		if (clientIp == null || clientIp.isEmpty()) {
			clientIp = "UNKNOWN";
		}
		
		// UserAgent
		String userAgent = exchange.getRequest().getHeaders().getFirst(USER_AGENT_HEADER);
		if (userAgent == null) {
        	userAgent = "UNKNOWN";
        } else if (userAgent.toLowerCase().contains("mobi")) {
        	userAgent = "Mobile";
        } else {
        	userAgent = "PC";
        }
		
		MDC.put("requestId", requestId);
		MDC.put("userId", userId);
		MDC.put("className", uri);
		MDC.put("methodName", method);
		MDC.put("executeResult", Long.toString(elapsedTime));
		MDC.put(MDC_CLIENT_IP, clientIp);
        MDC.put(MDC_USER_AGENT, userAgent);
		
		traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), MDC.get("executeResult"));
		// 설정 시간보다 느리면 slow 로깅
        if(elapsedTime > SLOW_PAGE_THRESHOLD_MS) {
        	slowLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), MDC.get("executeResult"));
        }
		
		MDC.clear();
	}

	public static class Config {
		// 필요 시 사용자 정의 설정 추가
	}

}