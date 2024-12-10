package com.example.neobns.logging.common;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger traceLogger = Logger.getLogger("TRACE");
	private static final Logger slowLogger = Logger.getLogger("SLOW");
	private static final Logger errorLogger = Logger.getLogger("ERROR");
	public static final long SLOW_PAGE_THRESHOLD_MS = 10; // slow page 기준, 나중에 환경 변수로...

	private static final String REQUEST_ID_HEADER = "X-Request-ID";
	private static final String USER_ID_HEADER = "X-User-ID";
	
	private static final String MDC_REQUEST_ID_KEY = "requestId";
	private static final String MDC_USER_ID_KEY = "userId";

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		String requestId = (String) MDC.get(MDC_REQUEST_ID_KEY);
		String userId = (String) MDC.get(MDC_USER_ID_KEY);
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
		
		traceLogger.info("[" + requestId + "] [" + uri + " : " + method + "]");
		
		MDC.remove("className");
        MDC.remove("methodName");

		try {
			ClientHttpResponse response = execution.execute(request, body);
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			
			MDC.put("className", uri);
			MDC.put("methodName", method);
			MDC.put("executeResult", Long.toString(elapsedTime));

			// After REST Call
			traceLogger.info("[" + requestId + "] [" + uri + " : " + method + "] [" + elapsedTime + "ms]");
			
			if(elapsedTime > SLOW_PAGE_THRESHOLD_MS) {
				slowLogger.info("[" + requestId + "] [" + uri + " : " + method + "] [" + elapsedTime + "ms]");
			}
            
			return response;
		} catch (Exception ex) {
			errorLogger.error("[" + requestId + "] [" + uri + " : " + method + "] [failed :" + ex.getMessage() + "]");
			throw ex;
		} finally {
			MDC.remove("className");
            MDC.remove("methodName");
            MDC.remove("executeResult");
		}

	}

}
