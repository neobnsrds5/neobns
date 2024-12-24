package com.example.neobns.logging.common; // transfer

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Intercepts({
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Profile("dev")
@Component
public class MybatisLoggingInterceptor implements Interceptor {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");
	private static final Logger slowLogger = LoggerFactory.getLogger("SLOW");
	private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
	public static final long SLOW_QUERY_THRESHOLD_MS = 0; // slow query 기준, 나중에 환경 변수로...
	
	// ${} 패턴 정규식
    private static final Pattern ILLEGAL_PATTERN = Pattern.compile("\\$\\{.*?\\}");

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 시작 시간 측정
		long start = System.currentTimeMillis();
		
		// 쿼리 정보 가져오기
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		
//		BoundSql boundSql = mappedStatement.getBoundSql(parameter);
//		String sql = boundSql.getSql().replaceAll("\\s+", " ").trim(); // 바운딩된 SQL
		
		SqlSource sqlSource = mappedStatement.getSqlSource();
        String rawSql = getRootSqlNode(sqlSource).replaceAll("\\s+", " ").trim(); // 바운딩 전 SQL
		MDC.put("queryLog", rawSql);
		
		Object result = null;
		try {
			// ${} 패턴 검사
			if (ILLEGAL_PATTERN.matcher(rawSql).find()) {
				throw new IllegalArgumentException("SQL에 허용되지 않는 ${} 패턴이 포함되어 있습니다: " + rawSql);
			} else {
				// 실제 쿼리 실행
				result = invocation.proceed();
			}
		} catch (Exception e){
	        MDC.put("className", "SQL");
	        MDC.put("methodName", rawSql);
			MDC.put("executeResult", e.getClass().getSimpleName());
			errorLogger.error("[{}] [{} : {}] [{}]", MDC.get("requestId"), "SQL", rawSql, e.getClass().getSimpleName());
		} finally {
			// 종료 시간 측정
			long elapsedTime = System.currentTimeMillis() - start;

			MDC.put("className", "SQL");
			MDC.put("methodName", rawSql);
			MDC.put("executeResult", Long.toString(elapsedTime));

			// SQL 실행 후 trace 로깅
			traceLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), "SQL", rawSql, elapsedTime);
			// 설정 시간보다 느리면 slow 로깅
			if (result != null && elapsedTime > SLOW_QUERY_THRESHOLD_MS) {
				slowLogger.info("[{}] [{} : {}] [{}ms]", MDC.get("requestId"), "SQL", rawSql, elapsedTime);
			}

			MDC.remove("className");
			MDC.remove("methodName");
			MDC.remove("executeResult");
		}
		return result;
	}
	
	private static String getRootSqlNode(SqlSource sqlSource) {
		
		try {
			// 리플렉션으로 rootSqlNode 접근
			if (sqlSource instanceof DynamicSqlSource) {
				Field field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
				field.setAccessible(true);
				SqlNode sqlNode = (SqlNode) field.get((DynamicSqlSource) sqlSource);

				StringBuilder sb = new StringBuilder();
				try {
					field = MixedSqlNode.class.getDeclaredField("contents");
					field.setAccessible(true);
					List<SqlNode> sqlNodeList = (List<SqlNode>) field.get((MixedSqlNode) sqlNode);
					for (SqlNode childeNode : sqlNodeList) {
						if (childeNode instanceof TextSqlNode) {
							field = TextSqlNode.class.getDeclaredField("text");
							field.setAccessible(true);
							sb.append((String) field.get((TextSqlNode) childeNode));
						}
					}

					return sb.toString();
				} catch (Exception e) {
					throw new RuntimeException();
				}
			} else {
				Field field = RawSqlSource.class.getDeclaredField("sqlSource");
				field.setAccessible(true);
				sqlSource = (SqlSource) field.get((RawSqlSource) sqlSource);

				field = StaticSqlSource.class.getDeclaredField("sql");
				field.setAccessible(true);
				return (String) field.get((StaticSqlSource) sqlSource);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
	
}
