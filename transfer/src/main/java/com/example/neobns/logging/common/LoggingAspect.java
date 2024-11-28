package com.example.neobns.logging.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
	
	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	/**
     * Controller 계층의 메서드 로깅
     */
    @Around("execution(* com.example.neobns.controller..*(..))")
    public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Controller");
    }

    /**
     * Service 계층의 메서드 로깅
     */
    @Around("execution(* com.example.neobns.service..*(..))")
    public Object logServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Service");
    }

    /**
     * MyBatis Mapper 계층의 메서드 로깅
     */
    @Around("execution(* com.example.neobns.mapper..*(..))")
    public Object logMapperLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Mapper");
    }

    /**
     * 공통 실행 로깅 메서드
     */
    private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = String.format("%s_%s", className, methodName);

        // 메서드 실행 전 로깅
        logger.info("{}; {}; {}; {}", MDC.get("requestId"), layer, fullMethodName, "start");

        Object result;
        try {
            result = joinPoint.proceed(); // 실제 메서드 실행
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;

            // 메서드 실행 후 로깅
            logger.info("{}, {}, {}, {}", MDC.get("requestId"), layer, fullMethodName, elapsedTime);
        }

        return result;
    }

}
