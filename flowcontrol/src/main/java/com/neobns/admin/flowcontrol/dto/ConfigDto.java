package com.neobns.admin.flowcontrol.dto;

public class ConfigDto {
    private long id;
    private String name;
    private String appName;
    private int maxConcurrentCalls;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    @Override
    public String toString() {
        return "ConfigDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", appName='" + appName + '\'' +
                ", maxConcurrentCalls=" + maxConcurrentCalls +
                ", limitForPeriod=" + limitForPeriod +
                ", limitRefreshPeriod=" + limitRefreshPeriod +
                ", timeoutDuration=" + timeoutDuration +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public void setLimitForPeriod(int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    public long getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    public void setLimitRefreshPeriod(long limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    public long getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setTimeoutDuration(long timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }
}
