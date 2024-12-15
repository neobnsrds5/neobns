package com.neobns.accounts.log;

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

	public static long SLOW_QUERY_THRESHOLD_MS = 0;

	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {

		MDC.put("className", "SQL");
		long start = System.currentTimeMillis();
		
		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Exception e) {
			errorLogger.error("{}; {}; {}; ", MDC.get("requestId"), "SQL", MDC.get("methodName"));
		}

		long elapsedTime = System.currentTimeMillis() - start;

		if (result == null) {
			MDC.remove("executeResult");
		} else {
			MDC.put("executeResult", Long.toString(elapsedTime));
		}

		traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);

		if (result != null && elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
			slowLogger.info("{}; {}; {}; {}", MDC.get("requestId"), "SQL", MDC.get("methodName"), elapsedTime);
		}

		MDC.remove("executeResult");
		MDC.remove("className");
		MDC.remove("methodName");

		return result;

	}

}
