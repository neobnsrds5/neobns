package com.example.neobns.logging.common; // quitstart

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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

	// 슬로우 쿼리 저장소
	private static final Queue<Map<String, Object>> SLOW_QUERIES = new ConcurrentLinkedQueue<>();
	
	// 슬로우 쿼리 저장소 최대 크기
	private static int SLOW_QUERIES_SIZE = 10;
	// 슬로우 쿼리 기준 시간
	public static long SLOW_QUERY_THRESHOLD_MS = 0;

	// 슬로우 쿼리 최근 10개 반환
	public static List<Map<String, Object>> getSlowQueries() {
		synchronized (SLOW_QUERIES) {
            return new ArrayList<>(SLOW_QUERIES);
        }
	}

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
			logger.info("[{}] SQL [{}] executed in {} ms", MDC.get("requestId"), sql, elapsedTime);

			if (elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
				Map<String, Object> slowQuery = new HashMap<>();
				slowQuery.put("requestId", MDC.get("requestId"));
				slowQuery.put("sql", sql);
				slowQuery.put("executeTime", elapsedTime);
				slowQuery.put("timestamp", new Date());
				
				synchronized (SLOW_QUERIES) {
					if(SLOW_QUERIES.size() >= SLOW_QUERIES_SIZE) {
						SLOW_QUERIES.poll(); // 오래된 데이터 제거
					}
					SLOW_QUERIES.add(slowQuery);
				}
			}
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
