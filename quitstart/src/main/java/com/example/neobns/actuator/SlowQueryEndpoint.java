package com.example.neobns.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import com.example.neobns.logging.common.MybatisLoggingInterceptor;

import java.util.List;
import java.util.Map;

@Component
@Endpoint(id = "slow-queries")
public class SlowQueryEndpoint {

    @ReadOperation
    public List<Map<String, Object>> getSlowQueries() {
        // 슬로우 쿼리 데이터를 반환
        return MybatisLoggingInterceptor.getSlowQueries();
    }
}