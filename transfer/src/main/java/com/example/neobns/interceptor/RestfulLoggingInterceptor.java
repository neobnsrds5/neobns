package com.example.neobns.interceptor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("dev") // 개발 환경에서만 활성화 (spring.profiles.active=dev 인 경우만 활성화)
public class RestfulLoggingInterceptor implements HandlerInterceptor {
	
	public static final Logger log = LoggerFactory.getLogger(RestfulLoggingInterceptor.class);
	
	// requestId 생성
	private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID_KEY = "requestId";
	
	// 실행 시간
	private long startTime;
	private long endTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	startTime = System.currentTimeMillis();
    	
    	// requestId 등록
    	String requestId = request.getHeader(REQUEST_ID_HEADER);
    	if(requestId == null || requestId.isEmpty()) {
    		requestId = UUID.randomUUID().toString();
    	}
    	MDC.put(MDC_REQUEST_ID_KEY, requestId);
    	response.setHeader(REQUEST_ID_HEADER, requestId);
    	
        // 요청 정보 로그 출력
    	log.info("[Request] [{}] [{}]", request.getRequestURI(), request.getMethod());
        return true; // 요청 계속 진행
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	endTime = System.currentTimeMillis();
        
    	// 응답 정보 로그 출력
    	String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        log.info("[Response] [{}ms] [{}] [{}]", (endTime-startTime), request.getRequestURI(), response.getStatus());
        
        // requestId 제거
        MDC.remove(MDC_REQUEST_ID_KEY);
    }
}
