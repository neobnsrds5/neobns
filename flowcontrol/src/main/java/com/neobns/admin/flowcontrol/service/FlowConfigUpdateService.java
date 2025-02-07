package com.neobns.admin.flowcontrol.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.BulkheadConfigDto;
import com.neobns.admin.flowcontrol.dto.ConfigDto;
import com.neobns.admin.flowcontrol.dto.DeleteConfigDto;
import com.neobns.admin.flowcontrol.dto.RateLimiterConfigDto;
import com.neobns.admin.flowcontrol.mapper.BulkheadMapper;
import com.neobns.admin.flowcontrol.mapper.RateLimiterMapper;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowConfigUpdateService {
    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ObjectMapper objectMapper;
    private final BulkheadMapper bulkheadMapper;
    private final RateLimiterMapper rateLimiterMapper;
    private final ConfigurationProp prop;


    public FlowConfigUpdateService(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry, ObjectMapper objectMapper,
                                   BulkheadMapper bulkheadMapper, RateLimiterMapper rateLimiterMapper, ConfigurationProp prop) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.objectMapper = objectMapper;
        this.bulkheadMapper = bulkheadMapper;
        this.rateLimiterMapper = rateLimiterMapper;
        this.prop = prop;
    }

    public void updateConfig(String message) {
        try{
            String application = prop.getName();

            List<BulkheadConfigDto> bulkheads = bulkheadMapper.findByApplication(application);
            List<RateLimiterConfigDto> rateLimiters = rateLimiterMapper.findByApplication(application);

            for (BulkheadConfigDto bulkhead : bulkheads) {
                String name;
                if (bulkhead.getType() == 0){
                    name = "global";
                } else {
                    name = bulkhead.getUrl();
                }

                BulkheadConfig newConfig = BulkheadConfig.custom()
                        .maxConcurrentCalls(bulkhead.getMaxConcurrentCalls())
                        .build();

                if (bulkheadRegistry.find(name).isPresent()) {
                    bulkheadRegistry.bulkhead(name).changeConfig(newConfig);
                } else {
                    bulkheadRegistry.bulkhead(name, newConfig);
                }
            }

            for (RateLimiterConfigDto rateLimiter : rateLimiters) {
                String name;
                if (rateLimiter.getType() == 0){
                    name = "global";
                } else {
                    name = rateLimiter.getMethod();
                }

                RateLimiterConfig newConfig = RateLimiterConfig
                        .custom()
                        .limitForPeriod(rateLimiter.getLimitForPeriod())
                        .timeoutDuration(java.time.Duration.ofMillis(rateLimiter.getTimeDuration()))
                        .limitRefreshPeriod(java.time.Duration.ofMillis(rateLimiter.getLimitRefreshPeriod()))
                        .build();

                if (rateLimiterRegistry.find(name).isPresent()) {
                    RateLimiter newRateLimiter = rateLimiterRegistry.rateLimiter(name, newConfig);
                    rateLimiterRegistry.replace(name, newRateLimiter);
                } else {
                    rateLimiterRegistry.rateLimiter(name, newConfig);
                }
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void deleteConfig(String message){
        try{
            DeleteConfigDto request = objectMapper.readValue(message, DeleteConfigDto.class);
            for (String url : request.getBulkheadNames()){
                if (bulkheadRegistry.find(url).isPresent()){
                    bulkheadRegistry.remove(url);
                }
            }

            for (String url : request.getRateLimiterNames()){
                if (rateLimiterRegistry.find(url).isPresent()){
                    rateLimiterRegistry.remove(url);
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}