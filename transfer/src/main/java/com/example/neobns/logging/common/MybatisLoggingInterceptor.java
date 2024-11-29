package com.example.neobns.logging.common; // transfer

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
@Profile("dev") // 개발 환경에서만 활성화
@Component
@RequiredArgsConstructor
public class MybatisLoggingInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(MybatisLoggingInterceptor.class);
	private final MeterRegistry meterRegistry;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		// 시작 시간 측정
		long start = System.currentTimeMillis();

		// 쿼리 정보 가져오기
		StatementHandler handler = (StatementHandler) invocation.getTarget();
		String sql = handler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();

		try {
			// SQL 실행 횟수 카운터 증가
			meterRegistry.counter("database.query.count", "sql", sql).increment();

			// 실제 쿼리 실행
			return invocation.proceed();
		} catch (Throwable e) {
			// SQL 에러 발생 카운터 증가
			meterRegistry.counter("database.query.errors", "sql", sql).increment();

			logger.error("{}; {}; {}; {}", MDC.get("requestId"), "SQL", sql, "error");
			throw e;
		} finally {
			// 종료 시간 측정
			long elapsedTime = System.currentTimeMillis() - start;

			meterRegistry.timer("database.query.execution.time", "sql", sql)
						.record(elapsedTime, java.util.concurrent.TimeUnit.MILLISECONDS);

			logger.info("{}; {}; {}; {}", MDC.get("requestId"), "SQL", sql, elapsedTime);
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// 필요 시 프로퍼티 설정
	}
}
