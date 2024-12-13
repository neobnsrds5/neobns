package com.example.neobns.service;

import java.sql.Time;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CacheService {
	
	@CachePut(value = "spider:time", key = "'lastReadTime'")
	public String setLastReadTime(String timeStmp) {
		System.out.println("setLastReadTime COMPLETE: " + timeStmp);
		return timeStmp;
	}
	
	@Cacheable(value = "spider:time", key = "'lastReadTime'")
	public String getLastReadTime() {
		System.out.println("getLastReadTime : 2000-01-01 00:00:00.000");
		String defaultTimestamp = "2000-01-01 00:00:00.000";
		return defaultTimestamp;
	}

}
