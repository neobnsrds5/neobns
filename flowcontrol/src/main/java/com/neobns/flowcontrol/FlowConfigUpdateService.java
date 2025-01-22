package com.neobns.flowcontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.flowcontrol.entity.FlowControlConfig;
import com.neobns.flowcontrol.repository.FlowControlConfigRepository;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlowConfigUpdateService {
    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ObjectMapper objectMapper;
    private final FlowControlProp flowControlProp;
    private final FlowControlConfigRepository repository;

    private final String applicationName;

    public FlowConfigUpdateService(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry, ObjectMapper objectMapper, FlowControlProp flowControlProp, FlowControlConfigRepository repository,@Value("${spring.application.name}") String applicationName) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.objectMapper = objectMapper;
        this.flowControlProp = flowControlProp;
        this.repository = repository;
        this.applicationName = applicationName;
    }

    public void updateBulkheadConfig(String message) {
        try{
            FlowConfigUpdateRequest request = objectMapper.readValue(message, FlowConfigUpdateRequest.class);
            System.out.println(request.toString());
            bulkheadRegistry.bulkhead("myServiceBulkhead").changeConfig(
                    BulkheadConfig.custom()
                            .maxConcurrentCalls(request.getMaxConcurrentCalls())
                            .build()
            );

            RateLimiterConfig newConfig = RateLimiterConfig
                    .custom()
                    .limitForPeriod(request.getLimitForPeriod())
                    .limitRefreshPeriod(java.time.Duration.ofMillis(request.getLimitRefreshPeriod()))
                    .timeoutDuration(java.time.Duration.ofMillis(request.getTimeoutDuration()))
                    .build();

            String rateLimiterName = "myServiceRateLimiter";
            RateLimiter rateLimiter = RateLimiter.of(rateLimiterName, newConfig);
            rateLimiterRegistry.replace("myServiceRateLimiter", rateLimiter);

            RateLimiter rl = rateLimiterRegistry.rateLimiter(rateLimiterName);
            Bulkhead bh = bulkheadRegistry.bulkhead("myServiceBulkhead");
            System.out.println("Bulkhead/RateLimiter config updated");
            System.out.println("max concurrent calls: " + bh.getBulkheadConfig().getMaxConcurrentCalls());
            System.out.println("limit for period: "+rl.getRateLimiterConfig().getLimitForPeriod());
            System.out.println("limit refresh period: "+rl.getRateLimiterConfig().getLimitRefreshPeriod());
            System.out.println("timeout duration: "+rl.getRateLimiterConfig().getTimeoutDuration());


        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateTopicConfig(String message) {
        try{
            FlowConfigUpdateRequestDto request = objectMapper.readValue(message, FlowConfigUpdateRequestDto.class);


            boolean isContained = request.getApplications().contains(applicationName);

            if (request.getType().equals("create")) {
                if (isContained) {
                    System.out.println("Bulkhead/Create/Topic config updated");
                }
            } else if (request.getType().equals("update")) {

            } else if (request.getType().equals("delete")) {

            } else {
                System.out.println("Invalid type name");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class FlowConfigUpdateRequestDto {
    private String type;
    private List<String> applications;
    private String topicName;
    private int maxConcurrentCalls;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    public String getType() {
        return type;
    }

    public List<String> getApplications() {
        return applications;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public long getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    public long getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public void setLimitForPeriod(int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    public void setLimitRefreshPeriod(long limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    public void setTimeoutDuration(long timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }
}