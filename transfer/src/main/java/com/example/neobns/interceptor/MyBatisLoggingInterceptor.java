package com.example.neobns.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Properties;

@Intercepts({
	// select문에 대해서만 수행 시간 측정
    @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
})
@Component // bean 등록
@Profile("dev") // 개발 환경에서만 활성화 (spring.profiles.active=dev 인 경우만 활성화)
public class MyBatisLoggingInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(MyBatisLoggingInterceptor.class);
	
    @Override
    public Object intercept(Invocation invocation) throws Throwable { // MyBatis의 특정 동작을 가로채 처리하는 메소드
        long startTime = System.currentTimeMillis(); // 시작 시간 측정
        try {
            return invocation.proceed(); // 실제 SQL 실행
        } finally {
            long endTime = System.currentTimeMillis(); // 종료 시간 측정
            
            // TODO: sql문이 출력이 안 돼...
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            String sql = handler.getBoundSql().getSql();
            
            // TODO: DB 저장까지 하면 Best
            log.info("[{}-SQL] [{}ms] []", MDC.get("requestId"), (endTime - startTime), sql);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this); // 플러그인으로 대상 객체를 감싸기
    }

    @Override
    public void setProperties(Properties properties) {
        // 플러그인 동작에 필요한 사용자 정의 프로퍼티 설정 가능
    }
}
