package com.example.neobns.logging.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class JPAQueryLoggingAspect {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
//	private static final Queue<Map<String, Object>> SLOW_QUERIES = new ConcurrentLinkedQueue<>();
//	private static int SLOW_QUERIES_SIZE = 10;
	public static long SLOW_QUERY_THRESHOLD_MS = 0;

//	public static List<Map<String, Object>> getSlowQueries() {
//		synchronized (SLOW_QUERIES) {
//			return new ArrayList<>(SLOW_QUERIES);
//		}
//	}

	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {

		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
//		String sql = joinPoint.getSignature().toString();
//		String shortSql = sql.substring(sql.lastIndexOf(".") + 1);
		
		MDC.put("executeResult", Long.toString(elapsedTime));
		MDC.put("className", "SQL");
		traceLogger.info("jpa {}; {}; {}; {}", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);

		if (elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
//			Map<String, Object> slowQuery = new HashMap<>();
//			slowQuery.put("requestID", MDC.get("requestId"));
//			slowQuery.put("methodName", MDC.get("sql"));
//			slowQuery.put("executeTime", elapsedTime);
//			slowQuery.put("timestamp", new Date());
			
			
			
			slowLogger.info("jpa {}; {}; {}; {}", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);

//			synchronized (SLOW_QUERIES) {
//				if (SLOW_QUERIES.size() >= SLOW_QUERIES_SIZE) {
//					SLOW_QUERIES.poll();
//				}
//				SLOW_QUERIES.add(slowQuery);
//				System.out.println("SLOW_QUERIES : " + SLOW_QUERIES.toString());
//			}
			
			MDC.remove("executeResult");
			MDC.remove("className");
			MDC.remove("methodName");
		}

		return result;

	}

}
