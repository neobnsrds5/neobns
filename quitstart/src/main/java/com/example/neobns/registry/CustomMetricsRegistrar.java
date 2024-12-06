package com.example.neobns.registry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.neobns.repository.LoggingEventRepository;

@Component
public class CustomMetricsRegistrar {

    private final MeterRegistry meterRegistry;
    private final LoggingEventRepository loggingEventRepository;

    public CustomMetricsRegistrar(MeterRegistry meterRegistry, LoggingEventRepository loggingEventRepository) {
        this.meterRegistry = meterRegistry;
        this.loggingEventRepository = loggingEventRepository;
    }

    @PostConstruct
    public void registerCustomMetrics() {
        // 데이터 가져오기
        List<Object[]> events = loggingEventRepository.findAllLoggingEvents();

        // 특정 데이터 카운트 및 메트릭 등록
        long errorCount = events.stream()
                .filter(event -> "ERROR".equals(event[2])) // execute_result가 ERROR인 경우
                .count();
        long infoCount = events.stream()
                .filter(event -> "INFO".equals(event[2])) // execute_result가 INFO인 경우
                .count();

        meterRegistry.gauge("logging_event_total_error", errorCount);
        meterRegistry.gauge("logging_event_total_info", infoCount);

        // logger_name 별 카운트 등록 (예: "com.example.Logger" 기준)
        events.stream()
                .map(event -> (String) event[1]) // logger_name
                .distinct()
                .forEach(loggerName -> {
                    long count = events.stream()
                            .filter(event -> loggerName.equals(event[1]))
                            .count();
                    meterRegistry.gauge("logging_event_logger_name_count", 
                        List.of(MeterRegistry.Tags.of("logger_name", loggerName)), count);
                });
    }
}
