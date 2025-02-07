package com.neobns.admin.flowcontrol.config;

import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.ConfigDto;
import com.neobns.admin.flowcontrol.mapper.ConfigMapper;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkheadConfig {

    private final ConfigMapper configMapper;
    private final ConfigurationProp prop;

    public BulkheadConfig(ConfigMapper configMapper, ConfigurationProp prop) {
        this.configMapper = configMapper;
        this.prop = prop;
    }

    @Bean
    public BulkheadRegistry bulkheadRegistry() {

        ConfigDto configDto = configMapper.findByAppName(prop.getName());
        int maxConcurrentCalls = (configDto!=null)?configDto.getMaxConcurrentCalls():25;

        io.github.resilience4j.bulkhead.BulkheadConfig customConfig = io.github.resilience4j.bulkhead.BulkheadConfig.custom()
                .maxConcurrentCalls(maxConcurrentCalls)
                .build();

        return BulkheadRegistry.of(customConfig);
    }
}
