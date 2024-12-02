package com.example.neobns.logging.common;

import java.util.regex.Pattern;

import org.hibernate.annotations.Comment;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JPASqlStatementInspector implements StatementInspector {

	private static final Logger logger = LoggerFactory.getLogger(JPASqlStatementInspector.class);
//	private static final Pattern COMMENTDELETE_PATTERN = Pattern.compile("\\\\/\\\\*.*?\\\\*\\\\/\\\\s*");

	@Override
	public String inspect(String sql) {

//		String returnedSql = COMMENTDELETE_PATTERN.matcher(sql).replaceAll("");
		String returnedSql = sql;

		MDC.put("lastSql", returnedSql);
		logger.info("lastsql : " + returnedSql);
		return returnedSql;

	}

}
