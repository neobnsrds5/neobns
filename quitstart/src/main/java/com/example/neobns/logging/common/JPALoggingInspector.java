package com.example.neobns.logging.common;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JPALoggingInspector implements StatementInspector {

	private static final Logger traceLogger = LoggerFactory.getLogger("TRACE");

	@Override
	public String inspect(String sql) {
		// jpa sql 저장 시 테이블 별칭, 칼럼네임 등 가독성이 좋지 않아 수정
		sql = sql.replaceAll("[a-z][0-9_]+(\\.|\\s)", "");
		MDC.put("methodName", sql);
		MDC.put("queryLog", sql);
		// inspector를 통해 추출한 sql문을 mdc에 넣어 로그가 찍히게 함
		if (MDC.get("requestId") != null) {
			traceLogger.info("[{}] [{} : {}]", MDC.get("requestId"), "SQL", MDC.get("methodName"));
		}

		return sql;
	}

}