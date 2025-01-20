package com.neobns.flowcontrol;

import io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    @Bean
    public RateLimiterConfigCustomizer rateLimiterConfigCustomizer() {
        return RateLimiterConfigCustomizer.of("myServiceRateLimiter", builder -> {
            builder.limitForPeriod(5)
                    .limitRefreshPeriod(java.time.Duration.ofSeconds(1))
                    .timeoutDuration(java.time.Duration.ofMillis(500));
        });
    }
}
