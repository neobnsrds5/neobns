package com.neobns.flowcontrol.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowControlConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String channelName;

    private String applications;

    private int maxConcurrentCalls;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    @Builder
    public FlowControlConfig(String channelName, List<String> applications, int maxConcurrentCalls, int limitForPeriod, long limitRefreshPeriod, long timeoutDuration) {
        this.channelName = channelName;
        StringBuilder sb = new StringBuilder();
        applications.forEach(app -> sb.append(app).append(","));
        this.applications = sb.toString();
        this.maxConcurrentCalls = maxConcurrentCalls;
        this.limitForPeriod = limitForPeriod;
        this.limitRefreshPeriod = limitRefreshPeriod;
        this.timeoutDuration = timeoutDuration;
    }
}
