package com.neobns.admin.flowcontrol.init;

import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.BulkheadConfigDto;
import com.neobns.admin.flowcontrol.dto.RateLimiterConfigDto;
import com.neobns.admin.flowcontrol.mapper.ApplicationMapper;
import com.neobns.admin.flowcontrol.mapper.RateLimiterMapper;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RateLimiterInitializer {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final ApplicationMapper applicationMapper;
    private final RateLimiterMapper rateLimiterMapper;
    private final ConfigurationProp prop;

    public RateLimiterInitializer(RateLimiterRegistry rateLimiterRegistry, ApplicationMapper applicationMapper, RateLimiterMapper rateLimiterMapper, ConfigurationProp prop) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.applicationMapper = applicationMapper;
        this.rateLimiterMapper = rateLimiterMapper;
        this.prop = prop;
    }

    @PostConstruct
    public void init(){
        if (applicationMapper.count(prop.getName()) == 0){
            return;
        }
        long id = applicationMapper.findIdByName(prop.getName());
        List<RateLimiterConfigDto> rateLimiters = rateLimiterMapper.findAll(id);
        for (RateLimiterConfigDto rateLimiter : rateLimiters) {
            RateLimiterConfig newConfig = RateLimiterConfig.custom()
                    .timeoutDuration(java.time.Duration.ofSeconds(rateLimiter.getTimeoutDuration()))
                    .limitRefreshPeriod(java.time.Duration.ofSeconds(rateLimiter.getLimitRefreshPeriod()))
                    .limitForPeriod(rateLimiter.getLimitForPeriod())
                    .build();
            rateLimiterRegistry.rateLimiter(rateLimiter.getUrl(), newConfig);
            RateLimiter rl = rateLimiterRegistry.rateLimiter(rateLimiter.getUrl());
//            System.out.println("name: " + rl.getName());
//            System.out.println("limit: " + rl.getRateLimiterConfig().getLimitForPeriod());
//            System.out.println("time: " + rl.getRateLimiterConfig().getLimitRefreshPeriod());
//            System.out.println("timeout: " + rl.getRateLimiterConfig().getTimeoutDuration());
//            System.out.println("===================================================");
        }
    }
}
