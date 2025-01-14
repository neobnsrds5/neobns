package com.example.neobns.logging.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
//	private final BlackListService blackListService;
//
//	public LoggingAspect(BlackListService blackListService) {
//		super();
//		this.blackListService = blackListService;
//	}

	/**
	 * Controller 계층의 메서드 로깅
	 */
	@Around("execution(* com.example.neobns.controller..*(..))")
	public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {

		// blacklist ip 차단 기능 주석처리
//		String mdcIp = MDC.get("clientIp") != null ? MDC.get("clientIp").trim() : "";
//		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
//				.currentRequestAttributes();
//		HttpServletRequest request = attributes.getRequest();
//		String realIp = blackListService.getRealIp(request);

//		System.out.println("mdc ip : " + mdcIp + " real ip : " + realIp + " " +  MDC.getCopyOfContextMap());

//		if (blackListService.isBlackListed(mdcIp) || blackListService.isBlackListed(realIp)) {
//			throw new SecurityException("black listed ip. MDC IP :" + mdcIp + " , Real IP :" + realIp);
//		}

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

		// 타임스탬프를 .SSSSSS 형식으로 변환
		String nanoTime = getNanoTime();
		MDC.put("nanoTime", nanoTime);

		long start = System.currentTimeMillis();
		String className = layer.equals("Mapper") ? joinPoint.getTarget().getClass().getInterfaces()[0].getName()
				: joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();

		MDC.put("className", className);
		MDC.put("methodName", methodName);

		// 메서드 실행 전 로깅
		traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"), "start");

		MDC.remove("className");
		MDC.remove("methodName");
		MDC.remove("nanoTime");

		try {
			return joinPoint.proceed(); // 실제 메서드 실행
		} finally {
			long elapsedTime = System.currentTimeMillis() - start;

			MDC.put("className", className);
			MDC.put("methodName", methodName);
			MDC.put("executeResult", Long.toString(elapsedTime));
			
			// 타임스탬프를 .SSSSSS 형식으로 변환
			nanoTime = getNanoTime();
			MDC.put("nanoTime", nanoTime);

			// 메서드 실행 후 trace 로깅
			traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), MDC.get("className"), MDC.get("methodName"),
					MDC.get("executeResult"));

			MDC.remove("className");
			MDC.remove("methodName");
			MDC.remove("executeResult");
			MDC.remove("nanoTime");
		}
	}

	private String getNanoTime() {

		// 타임스탬프를 .SSSSSS 형식으로 변환
		Instant now = Instant.now();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn");
		String nanoTime = localDateTime.format(dateTimeFormatter);

		return nanoTime;
	}

}
