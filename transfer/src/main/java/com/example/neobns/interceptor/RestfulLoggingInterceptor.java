package com.example.neobns.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("dev") // 개발 환경에서만 활성화 (spring.profiles.active=dev 인 경우만 활성화)
public class RestfulLoggingInterceptor implements HandlerInterceptor {
	
	public static final Logger log = LoggerFactory.getLogger(RestfulLoggingInterceptor.class);
	private long startTime;
	private long endTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	startTime = System.currentTimeMillis();
        // 요청 정보 로그 출력
    	log.info("[Request] [{}] [{}]", request.getRequestURI(), request.getMethod());
        return true; // 요청 계속 진행
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	endTime = System.currentTimeMillis();
        // 응답 정보 로그 출력
        log.info("[Response] [{}ms] [{}] [{}]", (endTime-startTime), request.getRequestURI(), response.getStatus());
    }
}
