package com.example.neobns.logging.common; // quitstart

import java.sql.Connection;
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
		@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
		})
@Profile("dev")
@Component
public class MybatisLoggingInterceptor implements Interceptor {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
	public static final long SLOW_QUERY_THRESHOLD_MS = 0; // slow query 기준, 나중에 환경 변수로...

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 시작 시간 측정
		StatementHandler handler = (StatementHandler) invocation.getTarget();
		long start = System.currentTimeMillis();
		// sql error 저장
		String errorSQL = handler.getBoundSql().getSql();
		MDC.put("queryLog", errorSQL.trim());

		try {
			// 실제 쿼리 실행
			return invocation.proceed();
		} finally {
			// 종료 시간 측정
			long elapsedTime = System.currentTimeMillis() - start;
			
			// 쿼리 정보 가져오기
			String sql = handler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();
			
			MDC.put("executeTime", Long.toString(elapsedTime));
			MDC.put("className", "SQL");
			MDC.put("methodName", sql);

			// SQL 실행 후 trace 로깅
			traceLogger.info("{}; {}; {}; {}", MDC.get("requestId"), "SQL", sql, elapsedTime);
			// 설정 시간보다 느리면 slow 로깅
			if (elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
				slowLogger.info("{}; {}; {}; {}", MDC.get("requestId"), "SQL", sql, elapsedTime);
			}
			
			MDC.remove("executeTime");
			MDC.remove("className");
			MDC.remove("methodName");
			MDC.remove("queryLog");
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
