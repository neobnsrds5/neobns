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
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
	public static final long SLOW_PAGE_THRESHOLD_MS = 10; // slow page 기준, 나중에 환경 변수로...

	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final String USER_ID_HEADER = "X-User-ID";
	
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	private static final String MDC_USER_ID_KEY = "userId";

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		String requestId = MDC.get(MDC_REQUEST_ID_KEY);
		String userId = MDC.get(MDC_USER_ID_KEY);
		String uri = request.getURI().toString();
		String method = request.getMethod().toString();

		if (requestId != null) {
			request.getHeaders().add(REQUEST_ID_HEADER, requestId);
		}

		if (userId != null) {
			request.getHeaders().add(USER_ID_HEADER, userId);
		}

		// Before REST Call
		long startTime = System.currentTimeMillis();
		
		MDC.put("className", uri);
		MDC.put("methodName", method);
		
		traceLogger.info("{}; {}; {}; {}; {}", requestId, uri, method, "start", userId);
		
		MDC.remove("className");
        MDC.remove("methodName");

		try {
			ClientHttpResponse response = execution.execute(request, body);
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			
			MDC.put("className", uri);
			MDC.put("methodName", method);
			MDC.put("executeTime", Long.toString(elapsedTime));

			// After REST Call
			traceLogger.info("{}; {}; {}; {}; {}", requestId, uri, method, elapsedTime, userId);
			
			if(elapsedTime > SLOW_PAGE_THRESHOLD_MS) {
				slowLogger.info("{}; {}; {}; {}; {}", requestId, uri, method, elapsedTime, userId);
			}
            
			return response;
		} catch (Exception ex) {
			traceLogger.error("{}; {}; {}; {}; {}", requestId, uri, method, "failed: " + ex.getMessage(), userId);
			throw ex;
		} finally {
			MDC.remove("className");
            MDC.remove("methodName");
            MDC.remove("executeTime");
		}

	}

}
