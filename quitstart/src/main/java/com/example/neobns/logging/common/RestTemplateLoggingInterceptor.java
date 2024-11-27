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
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        String uri = request.getURI().toString();
        
        if(requestId != null) {
        	request.getHeaders().add(REQUEST_ID_HEADER, requestId);
        }

        // Before REST Call
        long startTime = System.currentTimeMillis();
        logger.info("{}; {}; {}; {}", requestId, "REST call", uri, "start");

        try {
            ClientHttpResponse response = execution.execute(request, body);
            long elapsedTime = System.currentTimeMillis() - startTime;

            // After REST Call
            logger.info("{}; {}; {}; {}", requestId, "REST call", uri, elapsedTime);
            return response;
        } catch (Exception ex) {
            logger.error("{}; {}; {}; {}", requestId, "REST call", uri, "failed: "+ex.getMessage());
            throw ex;
        }
		
	}
	
}
