package com.neobns.flowcontrol;

public class FlowConfigUpdateRequest {
    private String name;
    private int maxConcurrentCalls;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "FlowConfigUpdateRequest{" +
                "name='" + name + '\'' +
                ", maxConcurrentCalls=" + maxConcurrentCalls +
                ", limitForPeriod=" + limitForPeriod +
                ", limitRefreshPeriod=" + limitRefreshPeriod +
                ", timeoutDuration=" + timeoutDuration +
                '}';
    }
}
