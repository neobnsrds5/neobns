package com.neobns.admin.flowcontrol.config;

import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.ConfigDto;
import com.neobns.admin.flowcontrol.mapper.ConfigMapper;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {
    private final ConfigMapper configMapper;
    private final ConfigurationProp prop;

    public RateLimiterConfig(ConfigMapper configMapper, ConfigurationProp prop) {
        this.configMapper = configMapper;
        this.prop = prop;
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        ConfigDto configDto = configMapper.findByAppName(prop.getName());
        if (configDto == null) {
            return RateLimiterRegistry.ofDefaults();
        } else {
            io.github.resilience4j.ratelimiter.RateLimiterConfig customConfig = io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                    .limitForPeriod(configDto.getLimitForPeriod())
                    .limitRefreshPeriod(java.time.Duration.ofMillis(configDto.getLimitRefreshPeriod()))
                    .timeoutDuration(java.time.Duration.ofMillis(configDto.getTimeoutDuration()))
                    .build();

            return RateLimiterRegistry.of(customConfig);
        }
    }
}