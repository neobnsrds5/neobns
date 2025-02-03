package com.neobns.admin.flowcontrol.dto;

import java.util.List;

public class DeleteConfigDto {
    private List<String> bulkheadNames;
    private List<String> rateLimiterNames;

    public List<String> getBulkheadNames() {
        return bulkheadNames;
    }

    public void setBulkheadNames(List<String> bulkheadNames) {
        this.bulkheadNames = bulkheadNames;
    }

    public List<String> getRateLimiterNames() {
        return rateLimiterNames;
    }

    public void setRateLimiterNames(List<String> rateLimiterNames) {
        this.rateLimiterNames = rateLimiterNames;
    }
}
