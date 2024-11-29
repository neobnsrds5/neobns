package com.example.neobns.logging.common;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFIlter extends OncePerRequestFilter{
	
	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	private static final String USER_ID_HEADER = "X-User-ID";
	private static final String MDC_USER_ID_KEY = "userId";
	private static final String MDC_USER_AGENT = "userAgent";
	private static final String MDC_USER_IP = "clientIp";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
//            requestId = UUID.randomUUID().toString(); // 새로운 Request ID 생성
        	requestId = "MISSED-ID";
        }
        
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.isEmpty()) {
//          requestId = UUID.randomUUID().toString(); // 새로운 Request ID 생성
        	userId = "MISSED-USER-ID"; 
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null||ip.isEmpty()) {
        	ip = "UNKNOWN";
        }
        
        String userAgent = request.getHeader("User-Agent");
        
        if (userAgent == null) {
        	System.out.println("유저 에이전트 정보를 확인할 수 없습니다.");
        	userAgent = "UNKNOWN DEVICE";
        } else if (userAgent.toLowerCase().contains("mobi")) {
        	userAgent = "Mobile";
        } else {
        	userAgent = "PC";
        }
        
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        MDC.put(MDC_USER_ID_KEY, userId);
        MDC.put(MDC_USER_IP, ip);
        MDC.put(MDC_USER_AGENT, userAgent);

        // 응답 헤더에 Request ID 추가
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(USER_ID_HEADER, userId);
        response.setHeader("X-Forwarded-For", ip);
        response.setHeader("User-Agent", userAgent);
        

        try {
            filterChain.doFilter(request, response);
        } finally {
            // MDC 정리
            MDC.clear();
        }
    }

}
