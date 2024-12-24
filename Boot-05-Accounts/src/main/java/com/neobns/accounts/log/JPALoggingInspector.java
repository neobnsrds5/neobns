package com.neobns.accounts.log;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class JPALoggingInspector implements StatementInspector{

	@Override
	public String inspect(String sql) {
		// jpa sql 저장 시 테이블 별칭, 칼럼네임 등 가독성이 좋지 않아 수정
		sql = sql.replaceAll("[a-z][0-9_]+(\\.|\\s)", "");
		MDC.put("queryLog", sql);
		return sql;
	}

}