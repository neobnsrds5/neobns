package com.example.neobns.logging.common;

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

@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
@Profile("dev")
@Component
public class MybatisLoggingInterceptor implements Interceptor {
	private static final Logger logger = LoggerFactory.getLogger(MybatisLoggingInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 시작 시간 측정
		long start = System.currentTimeMillis();

		try {
			// 실제 쿼리 실행
			return invocation.proceed();
		} finally {
			// 종료 시간 측정
			long elapsedTime = System.currentTimeMillis() - start;

			// 쿼리 정보 가져오기
			StatementHandler handler = (StatementHandler) invocation.getTarget();
			String sql = handler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();

			// 로깅
			logger.info("[{}] Executed SQL: [{}] executed in {} ms", MDC.get("requestId"), sql, elapsedTime);
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
