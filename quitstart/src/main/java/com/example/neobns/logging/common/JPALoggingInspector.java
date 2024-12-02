package com.example.neobns.logging.common;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JPALoggingInspector implements StatementInspector{

	@Override
	public String inspect(String sql) {
		System.out.println("sql executed : "  + sql);
		MDC.put("sql", sql);
		return sql;
	}

}
