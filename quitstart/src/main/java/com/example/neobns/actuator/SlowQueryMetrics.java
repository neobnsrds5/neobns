package com.example.neobns.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.neobns.logging.common.MybatisLoggingInterceptor;

@Component
public class SlowQueryMetrics {

    private final MeterRegistry meterRegistry;

    public SlowQueryMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 슬로우 쿼리 메트릭 업데이트
    public void updateMetrics() {
        Map<String, Long> slowQueries = MybatisLoggingInterceptor.getSlowQueries();
        slowQueries.forEach((sql, executionTime) -> {
            Timer.builder("slow_query_execution_time")
                    .description("Execution time of slow queries")
                    .tags(Tags.of("query", sql))
                    .register(meterRegistry)
                    .record(executionTime, java.util.concurrent.TimeUnit.MILLISECONDS);
        });
    }
}
