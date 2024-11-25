package com.example.neobns.logging.common;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Intercepts({
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class MybatisLoggingInterceptor implements Interceptor{
	private static final Logger logger = LoggerFactory.getLogger(MybatisLoggingInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            logger.info("[{}] SQL executed in {}ms", MDC.get("requestId"), elapsedTime);
        }
	}

	@Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // Custom properties if needed
    }
}
