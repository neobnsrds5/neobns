package com.neobns.flowcontrol;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.common.bulkhead.configuration.BulkheadConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkheadConfig {
    @Bean
    public BulkheadConfigCustomizer customBulkheadConfig(){
        return BulkheadConfigCustomizer.of("myServiceBulkhead", builder -> builder.maxConcurrentCalls(10).build());
    }

    @Bean
    public BulkheadRegistry bulkheadRegistry(){
        return BulkheadRegistry.ofDefaults();
    }
}
