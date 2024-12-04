package com.example.neobns.logging.common;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JPALoggingInspector implements StatementInspector{

	@Override
	public String inspect(String sql) {
		sql = sql.replaceAll("[a-z][0-9_]+(\\.|\\s)", "");
		System.out.println("sql executed : "  + sql);
		MDC.put("methodName", sql);
		MDC.put("queryLog", sql);
		return sql;
	}

}
