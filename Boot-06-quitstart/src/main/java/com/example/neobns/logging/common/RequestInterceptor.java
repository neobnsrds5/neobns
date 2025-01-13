package com.example.neobns.logging.common;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor implements HandlerInterceptor {

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
	
	private static final long SLOW_PAGE_THRESHOLD_MS = 1000; // slow page 기준, 나중에 환경 변수로 빼기..!

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		String requestId = request.getHeader(REQUEST_ID_HEADER);
		boolean isDirectRequest = false;

		if (requestId == null || requestId.isEmpty()) {
			requestId = generateRandomRequestId(); // 새로운 Request ID 생성
			isDirectRequest = true; // 게이트웨이 거치지 않은 요청인 경우 처음, 마지막 로깅 추가 필요
//        	requestId = "MISSED-ID";
		}

		String userId = request.getHeader(USER_ID_HEADER);
		if (userId == null || userId.isEmpty()) {
			userId = generateRandomUserId(); // 새로운 user id 부여
//        	userId = "MISSED-USER-ID"; 
		}

		String clientIp = request.getHeader(CLIENT_IP_HEADER);
		if (clientIp == null || clientIp.isEmpty()) {
			clientIp = request.getRemoteAddr();

			if (clientIp == null || clientIp.isEmpty()) {
				clientIp = "UNKNOWN";
			}
		}

		String userAgent = request.getHeader(USER_AGENT_HEADER);
		if (userAgent == null) {
			userAgent = "UNKNOWN";
		} else if (userAgent.toLowerCase().contains("mobi")) {
			userAgent = "Mobile";
		} else {
			userAgent = "PC";
		}

		// MDC에 저장
		MDC.put(MDC_REQUEST_ID_KEY, requestId);
		MDC.put(MDC_USER_ID_KEY, userId);
		MDC.put(MDC_CLIENT_IP, clientIp);
		MDC.put(MDC_USER_AGENT, userAgent);

		// Response 헤더에 추가 (후속 요청을 위해)
		response.setHeader(REQUEST_ID_HEADER, requestId);
		response.setHeader(USER_ID_HEADER, userId);

		MDC.put("startTime", String.valueOf(System.currentTimeMillis()));

		System.out.println("pre : MDC.getCopyOfContextMap : " + MDC.getCopyOfContextMap().toString());
		
		String uri = request.getRequestURL().toString();
		String method = request.getMethod().toString();
		
		MDC.put("className", uri);
		MDC.put("methodName", method);
		
		traceLogger.info("[{}] [{} : {}] [{}]", MDC.get("requestId"), uri, method, "start");

		return true;
		// 요청을 다음 단계로 전달할지에 대한 여부. true면 요청 처리 계속 진행하여 컨트롤러 호출.
		// false면 요청 처리 중단하고 응답을 직접 작성하거나 다른 작업 수행
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		
		String uri = request.getRequestURL().toString();
		String method = request.getMethod().toString();
		
		MDC.put("className", uri);
		MDC.put("methodName", method);

		long executeTime = System.currentTimeMillis() - Long.parseLong(MDC.get("startTime"));

		MDC.put("executeResult", String.valueOf(executeTime));

		System.out.println("after : MDC.getCopyOfContextMap : " + MDC.getCopyOfContextMap().toString());
		
		traceLogger.info("[{}] [{} : {}] [{}]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), MDC.get("executeResult"));
		// 설정 시간보다 느리면 slow 로깅
        if(executeTime > SLOW_PAGE_THRESHOLD_MS) {
        	slowLogger.info("[{}] [{} : {}] [{}]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), MDC.get("executeResult"));
        }
		
//		MDC.clear();

		// 요청 완료 후 MDC에서 제거
		MDC.remove(MDC_REQUEST_ID_KEY);
		MDC.remove(MDC_USER_ID_KEY);
		MDC.remove(MDC_CLIENT_IP);
		MDC.remove(MDC_USER_AGENT);

	}

	private String generateRandomRequestId() {
		// 게이트 웨이 거치지 않은 고유 Request ID 생성 로직
		return "missed-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private String generateRandomUserId() {
		// 게이트 웨이 거치지 않은 고유 User ID 생성 로직
		return "missed-user-" + UUID.randomUUID().toString().substring(0, 8);
	}

}
