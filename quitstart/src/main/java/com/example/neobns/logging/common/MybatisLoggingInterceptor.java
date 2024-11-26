package com.example.neobns.logging.common; // quitstart

import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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

	// 슬로우 쿼리 저장소, 동시성 문제 해결을 위해 ConcurrentHashMap 사용
	// 일단은 SQL문과 걸린 시간만 출력할 수 있도록 구현
	// 추후 requestId 등 필요한 정보 추가하기
	private static final ConcurrentHashMap<String, Long> slowQueryStore = new ConcurrentHashMap<>();

	// 슬로우 쿼리 기준 시간 - 추후 properties로 빼기
	public static final long SLOW_QUERY_THRESHOLD_MS = 0;

	// 슬로우 쿼리 상위 10개 반환
	public static Map<String, Long> getSlowQueries() {
		return slowQueryStore.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
				.limit(10).collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), Map::putAll);
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
				slowQueryStore.put(sql, elapsedTime);

				slowQueryStore.forEach((key, value) -> {
					System.out.println(key + " : " + value);
				});
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
