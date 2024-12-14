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
		sql = sql.replaceAll("[a-z][0-9_]+(\\.|\\s)", "");
		MDC.put("methodName", sql);
		MDC.put("queryLog", sql);

		if (MDC.get("requestId") != null) {
			traceLogger.info("[{}] [{} : {}]", MDC.get("requestId"), "SQL", MDC.get("methodName"));
		}

		return sql;
	}

}
