package com.neobns.admin.flowcontrol.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.*;
import com.neobns.admin.flowcontrol.mapper.BulkheadMapper;
import com.neobns.admin.flowcontrol.mapper.RateLimiterMapper;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class FlowConfigUpdateService {
    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ObjectMapper objectMapper;
    private final BulkheadMapper bulkheadMapper;
    private final RateLimiterMapper rateLimiterMapper;


    public FlowConfigUpdateService(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry, ObjectMapper objectMapper,
                                   BulkheadMapper bulkheadMapper, RateLimiterMapper rateLimiterMapper) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.objectMapper = objectMapper;
        this.bulkheadMapper = bulkheadMapper;
        this.rateLimiterMapper = rateLimiterMapper;
    }

    public void updateConfig(String message) {
        try{
            UpdateConfigDto updateConfigDto = objectMapper.readValue(message, UpdateConfigDto.class);

            long id = updateConfigDto.getId();
            int doing = updateConfigDto.getDoing();
            if (updateConfigDto.getType() == 0){
                //bulkhead
                String url = updateConfigDto.getName();
                if (doing == 0){
                    BulkheadConfigDto configDto = bulkheadMapper.findById(id);
                    BulkheadConfig newConfig = BulkheadConfig.custom()
                            .maxConcurrentCalls(configDto.getMaxConcurrentCalls())
                            .maxWaitDuration(Duration.ofSeconds(configDto.getMaxWaitDuration()))
                            .build();

                    if (bulkheadRegistry.find(url).isPresent()){
                        //update
                        bulkheadRegistry.bulkhead(url).changeConfig(newConfig);
                    } else {
                        //create
                        bulkheadRegistry.bulkhead(url, newConfig);
                    }
                } else {
                    //delete
                    if(bulkheadRegistry.find(url).isPresent()){
                        bulkheadRegistry.remove(url);
                    }
                }
            } else if (updateConfigDto.getType() == 1){
                //rateLimiter
                String url = updateConfigDto.getName();
                if (doing == 0){
                    RateLimiterConfigDto configDto = rateLimiterMapper.findById(id);

                    RateLimiterConfig newConfig = RateLimiterConfig.custom()
                            .limitForPeriod(configDto.getLimitForPeriod())
                            .limitRefreshPeriod(java.time.Duration.ofSeconds(configDto.getLimitRefreshPeriod()))
                            .timeoutDuration(java.time.Duration.ofSeconds(configDto.getTimeoutDuration()))
                            .build();


                    if (rateLimiterRegistry.find(url).isPresent()){
                        //update
                        RateLimiter newRateLimiter = RateLimiter.of(url, newConfig);
                        rateLimiterRegistry.replace(url, newRateLimiter);
                    } else {
                        //create
                        rateLimiterRegistry.rateLimiter(url, newConfig);
                    }
                } else {
                    //delete
                    if (rateLimiterRegistry.find(url).isPresent()){
                        rateLimiterRegistry.remove(url);
                    }
                }
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}