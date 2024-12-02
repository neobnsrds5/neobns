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
	private static final String USER_ID_HEADER = "X-User-ID";
	private static final String MDC_USER_ID_KEY = "userId";
	private static final String MDC_USER_AGENT = "userAgent";
	private static final String MDC_USER_IP = "clientIp";

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestId = request.getHeader(REQUEST_ID_HEADER);
		
        if (requestId == null || requestId.isEmpty()) {
//            requestId = UUID.randomUUID().toString(); // 새로운 Request ID 생성
        	requestId = "MISSED-ID";
        }
        
        String userId = request.getHeader(USER_ID_HEADER);
	        if (userId == null || userId.isEmpty()) {
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
        
        

        // Request ID, User ID를 MDC에 저장
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        MDC.put(MDC_USER_ID_KEY, userId);
        MDC.put(MDC_USER_IP, ip);
        MDC.put(MDC_USER_AGENT, userAgent);

        // Response 헤더에 Request ID 추가 (후속 요청을 위해)
        // 게이트웨이에서부터 추적하려면, 게이트웨이는 WebFlux 기반이지만, response.setHeader()은 동기 방식인
        //HttpServletResponse의 메서드이므로 동작하지 않음.
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(USER_ID_HEADER, userId);
        
        return true; //요청을 다음 단계로 전달할지에 대한 여부. true면 요청 처리 계속 진행하여 컨트롤러 호출. false면 요청 처리 중단하고 응답을 직접 작성하거나 다른 작업 수행
    }

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 요청 완료 후 MDC에서 제거
		MDC.remove(MDC_REQUEST_ID_KEY);
		MDC.remove(MDC_USER_ID_KEY);
		MDC.remove(MDC_USER_IP);
		MDC.remove(MDC_USER_AGENT);
	}
}
