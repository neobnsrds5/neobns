package com.example.neobns.logging.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class JPAQueryLoggingAspect {

	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {

		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long elapsedTime = System.currentTimeMillis() - start;
		String sql = joinPoint.getSignature().toString();
		String shortSql = sql.substring(sql.lastIndexOf(".")+1);
		MDC.put("sql2", shortSql);
		System.out.println("mdc : " + MDC.getCopyOfContextMap());
		log.info("jpa aspect: {}; {}; {}; {}", MDC.get("requestId"), "SQL", shortSql, elapsedTime);

		return result;

	}

}
