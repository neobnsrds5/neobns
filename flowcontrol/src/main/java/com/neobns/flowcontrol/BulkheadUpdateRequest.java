package com.neobns.flowcontrol;

public class BulkheadUpdateRequest {
    private String name;
    private int maxConcurrentCalls;

    public String getName(){
        return name;
    }

    public int getMaxConcurrentCalls(){
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls){
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    public void setName(String name){
        this.name = name;
    }
}
