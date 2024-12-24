package com.example.neobns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class TimerConfig {

	@Bean
	TimedAspect timedAspect(MeterRegistry meterRegistry) {
		// Micrometer의 @Timed 애노테이션을 사용해 메서드 실행 시간을 actuator를 통해 측정하도록 설정
		return new TimedAspect(meterRegistry);
	}

}
