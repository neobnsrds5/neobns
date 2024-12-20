package com.example.neobns.logging.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.thymeleaf.standard.expression.AdditionSubtractionExpression;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class JPAQueryLoggingAspect {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
	private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
	public static long SLOW_QUERY_THRESHOLD_MS = 0;

	// repository 접근 시 실행되어 슬로우 / 정상 / 에러 상태에 따라 다른 로그 남김
	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {

		// 레포짓토리 실행 시작 시간
		long repositoryStart = System.currentTimeMillis();


		// com.example.neobns.repository.CustomerRepository.findByMobileNumber(String)
		String repositoryString = joinPoint.getSignature().toString().replaceAll(".*?\s", "").trim();
		String[] arrays = repositoryString.split("\\.");
		String methodName = joinPoint.getSignature().getName();
//		String className = arrays[4];
		String className = repositoryString.substring(0, repositoryString.lastIndexOf("."));

		// CustomerRepository
		MDC.put("className", className);
		// findByMobileNumber
		MDC.put("methodName", methodName);

		// sql 실행 전 레포짓토리 로깅
		traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), "start");

		long start = System.currentTimeMillis();

		Object result = null;
		try {
			// SQL 실행 시작 시간
			result = joinPoint.proceed();
		} catch (Exception e) {
			MDC.put("className", "SQL");
			MDC.put("methodName", MDC.get("queryLog"));
			MDC.put("executeResult", e.getClass().getSimpleName());
			errorLogger.error("[{}] [{} : {}] [{}]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
					MDC.get("executeResult"));
		}

		long sqlElapsedTime = System.currentTimeMillis() - start;
		MDC.put("className", "SQL");
		MDC.put("methodName", MDC.get("queryLog"));
		MDC.put("executeResult", Long.toString(sqlElapsedTime));

		traceLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
				MDC.get("executeResult"));

		if (result != null && sqlElapsedTime > SLOW_QUERY_THRESHOLD_MS) {
			slowLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
					MDC.get("executeResult"));
		}

		long repositoryEnd = System.currentTimeMillis();
		// 리포짓토리 총 수행시간
		long repoElapsedTime = System.currentTimeMillis() - repositoryStart;

		MDC.put("className", className);
		MDC.put("methodName", methodName);
		MDC.put("executeResult", Long.toString(repoElapsedTime));

		traceLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
				MDC.get("executeResult"));

		MDC.remove("executeResult");
		MDC.remove("className");
		MDC.remove("methodName");

		return result;

	}

}