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

		
		System.out.println("instead of sql");
		
		System.out.println(joinPoint.getSignature().toString());
		String repositoryString = joinPoint.getSignature().toString();
		String[] arrays = repositoryString.split("\\.");
		System.out.println(arrays.length);
		System.out.println(arrays[arrays.length-1]);
		String methodName = joinPoint.getSignature().getName();

//		Optional com.example.neobns.repository.CustomerRepository.findByMobileNumber(String)

		String className = arrays[4];
		MDC.put("className", className);
		MDC.put("methodName", methodName);

		long start = System.currentTimeMillis();

		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Exception e) {
			MDC.put("executeResult", e.getClass().getSimpleName());
			errorLogger.error("[{}] [{} : {}] [{}]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
					e.getClass().getSimpleName());
		}

		long elapsedTime = System.currentTimeMillis() - start;
		MDC.put("executeResult", Long.toString(elapsedTime));

		traceLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
				elapsedTime);

		if (result != null && elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
			slowLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
					elapsedTime);
		}

		MDC.remove("executeResult");
		MDC.remove("className");
		MDC.remove("methodName");

		return result;

	}

}