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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
//            requestId = UUID.randomUUID().toString(); // 새로운 Request ID 생성
        	requestId = "MISSED-ID";
        }
        MDC.put("requestId", requestId);

        // 응답 헤더에 Request ID 추가
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // MDC 정리
            MDC.clear();
        }
    }

}
