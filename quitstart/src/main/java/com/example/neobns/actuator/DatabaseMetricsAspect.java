//package com.example.neobns.actuator;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Timer;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Aspect // AOP 역할 수행토록
//@Component // Bean 등록
//public class DatabaseMetricsAspect {
//
//	// 메트릭 기록하는데 사용
//	private final MeterRegistry meterRegistry;
//
//	public DatabaseMetricsAspect(MeterRegistry meterRegistry) {
//		this.meterRegistry = meterRegistry;
//	}
//
//	// 특정 메소드 패턴을 지정하여 해당하는 메소드 호출 전후에 실행되도록
//	// MyBatis, JDBC, JPA 모두 적용 가능하도록
//	@Around("execution(* org.mybatis.spring.SqlSessionTemplate.*(..)) || "
//			+ "execution(* javax.sql.DataSource.*(..)) || "
//			+ "execution(* org.springframework.data.jpa.repository.JpaRepository.*(..))")
//	public Object monitorDatabaseOperations(ProceedingJoinPoint joinPoint) throws Throwable {
//		// Timer 샘플 시작
//		Timer.Sample sample = Timer.start(meterRegistry); // SQL 실행 시간 측정
//
//		try {
//			// 쿼리 실행 횟수 카운터 증가
//			meterRegistry.counter("database.query.count",
//									"class", joinPoint.getTarget().getClass().getSimpleName(),
//									"method", joinPoint.getSignature().getName()).increment();
//
//			// 실제 메소드 실행
//			return joinPoint.proceed();
//		} catch (Throwable e) {
//			// 에러 발생 시 카운터 증가
//			meterRegistry.counter("database.query.errors",
//									"class", joinPoint.getTarget().getClass().getSimpleName(),
//									"method", joinPoint.getSignature().getName()).increment();
//			throw e;
//		} finally {
//			// Timer로 SQL 실행 시간 기록
//			sample.stop(meterRegistry.timer("database.query.execution.time",
//											"class", joinPoint.getTarget().getClass().getSimpleName(),
//											"method", joinPoint.getSignature().getName()));
//		}
//	}
//}
