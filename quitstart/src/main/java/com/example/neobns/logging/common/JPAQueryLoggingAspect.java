package com.example.neobns.logging.common;

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
	private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
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

		MDC.put("className", "SQL");
		long start = System.currentTimeMillis();
		
		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Exception e) {
			MDC.put("executeResult", e.getClass().getSimpleName());
			errorLogger.error("[{}] [{} : {}] [{}]", MDC.get("requestId"), "SQL", MDC.get("methodName"), e.getClass().getSimpleName());
		}

		long elapsedTime = System.currentTimeMillis() - start;
		MDC.put("executeResult", Long.toString(elapsedTime));

		traceLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);

		if (result != null && elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
			slowLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);
		}

		MDC.remove("executeResult");
		MDC.remove("className");
		MDC.remove("methodName");

		return result;

	}

}
