package com.example.neobns.logging.common;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor implements HandlerInterceptor {

	 private static final String REQUEST_ID_HEADER = "X-Request-ID";
	    private static final String MDC_REQUEST_ID_KEY = "requestId";
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
//            requestId = UUID.randomUUID().toString(); // 새로운 Request ID 생성
        	requestId = "MISSED-ID";
        }

        // Request ID를 MDC에 저장
        MDC.put(MDC_REQUEST_ID_KEY, requestId);

        // Response 헤더에 Request ID 추가 (후속 요청을 위해)
        // 게이트웨이에서부터 추적하려면, 게이트웨이는 WebFlux 기반이지만, response.setHeader()은 동기 방식인
        //HttpServletResponse의 메서드이므로 동작하지 않음.
//        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        return true;
    }
	
	@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 요청 완료 후 MDC에서 제거
        MDC.remove(MDC_REQUEST_ID_KEY);
    }
}
