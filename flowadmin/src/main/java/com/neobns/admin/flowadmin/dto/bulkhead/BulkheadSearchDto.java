package com.neobns.admin.flowadmin.dto.bulkhead;

public class BulkheadSearchDto {
    private long id;
    private String application_name;
    private String url;
    private int maxConcurrentCalls;
    private int maxWaitDuration;

    public int getMaxWaitDuration() {
        return maxWaitDuration;
    }

    public void setMaxWaitDuration(int maxWaitDuration) {
        this.maxWaitDuration = maxWaitDuration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }
}
