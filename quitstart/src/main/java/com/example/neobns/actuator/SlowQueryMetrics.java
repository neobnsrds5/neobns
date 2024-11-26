package com.example.neobns.actuator;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.util.List;
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
		List<Map<String, Object>> slowQueries = MybatisLoggingInterceptor.getSlowQueries();

		for (Map<String, Object> query : slowQueries) {
			// 메트릭에 사용할 데이터 추출
			String requestId = (String) query.get("requestId");
			String sql = (String) query.get("sql");
			long executeTime = (long) query.get("executeTime");
			String timestamp = query.get("timestamp").toString();

			// 실행 시간 메트릭 갱신 또는 등록
			Gauge.builder("slow_query_execution_time", query, q -> executeTime)
					.description("Execution time of a slow query").tags(createTags(requestId, sql, timestamp))
					.register(meterRegistry);
		}
	}

	// 태그 생성 메서드
	private Iterable<Tag> createTags(String requestId, String sql, String timestamp) {
		return Tags.of(
				Tag.of("requestId", requestId),
				Tag.of("sql", sql),
				Tag.of("timestamp", timestamp));
	}
}
