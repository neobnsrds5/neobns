package com.example.neobns.logging.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor{
	
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);
	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	private static final String USER_ID_HEADER = "X-User-ID";
	private static final String MDC_USER_ID_KEY = "userId";
	private static final String MDC_USER_AGENT = "userAgent";
	private static final String MDC_USER_IP = "clientIp";
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        String uri = request.getURI().toString();
        
        String userId = MDC.get(MDC_USER_ID_KEY);
        String userUri = request.getURI().toString();
        
        String clientIp = MDC.get(MDC_USER_IP);
        String userAgent = MDC.get(MDC_USER_AGENT);
        
        if(requestId != null) {
        	request.getHeaders().add(REQUEST_ID_HEADER, requestId);
        }
        
        if(userId != null) {
        	request.getHeaders().add(USER_ID_HEADER, userId);
        }
        
        if (clientIp != null && !clientIp.isEmpty()) {
        	request.getHeaders().set("X-Forwarded-For", clientIp);
        }
        
        if (userAgent!= null && !clientIp.isEmpty()) {
        	request.getHeaders().set("User-Agent", userAgent);
        }

        // Before REST Call
        long startTime = System.currentTimeMillis();
        logger.info("{}; {}; {}; {}; {}", requestId, "REST call", uri, "start", userId);

        try {
            ClientHttpResponse response = execution.execute(request, body);
            long elapsedTime = System.currentTimeMillis() - startTime;

            // After REST Call
            logger.info("{}; {}; {}; {}; {}", requestId, "REST call", uri, elapsedTime, userId);
            return response;
        } catch (Exception ex) {
            logger.error("{}; {}; {}; {}; {}", requestId, "REST call", uri, "failed: "+ex.getMessage(), userId);
            throw ex;
        }
		
	}
	
}
