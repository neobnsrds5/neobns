package com.example.neobns.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// spring boot 사용 시 기본적으로 aop가 활성화 되므로 config class 안 만들어도 됨
@Aspect // aop 기능 구현을 위한 어노테이션
@Component
@Profile("dev") // 개발 환경에서만 활성화 (spring.profiles.active=dev 인 경우만 활성화)
public class PerformanceLoggingAspect {
	
	private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);

	// pointcut 정의
	// com.example.neobns.controller 안에 있는 모든 메소드에 적용
    @Around("execution(* com.example.neobns.controller..*(..))")
    public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {
    	logExecutionMethod(joinPoint, "Controller");
        return logExecutionTime(joinPoint, "Controller");
    }

    @Around("execution(* com.example.neobns.service..*(..))")
    public Object logServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
    	logExecutionMethod(joinPoint, "Service");
        return logExecutionTime(joinPoint, "Service");
    }

    @Around("execution(* com..example.neobns.mapper..*(..))")
    public Object logMyBatisLayer(ProceedingJoinPoint joinPoint) throws Throwable {
    	logExecutionMethod(joinPoint, "MyBatis");
        return logExecutionTime(joinPoint, "MyBatis");
    }
    
    // 실행 전 메소드 로그
    private void logExecutionMethod(ProceedingJoinPoint joinPoint, String layer) {
    	log.info("[{}] [{}]", layer, joinPoint.getSignature());
    }
    // 실행 후 메소드 실행 시간 로그
    private Object logExecutionTime(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("[{}] [{}ms] [{}]", layer, (endTime - startTime), joinPoint.getSignature());
        }
    }
}
