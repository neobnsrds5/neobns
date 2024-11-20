package com.neo.adminserver.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LoggerService implements HealthIndicator{
	private final String LOGGER_SERVICE = "LoggerService";

	@Override
	public Health health() {
		if (isLoggerServiceGood()) {
			// withDetails를 이용해 맵으로 여러개 전달 가능
			return Health.up().withDetail(LOGGER_SERVICE, "Service is running").build();
		}
		return Health.down().withDetail(LOGGER_SERVICE, "Service is not available").build();
	}

	private boolean isLoggerServiceGood() {
		// logic
		return true;

	}
	
}
