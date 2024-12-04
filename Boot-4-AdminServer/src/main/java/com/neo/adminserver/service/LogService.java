package com.neo.adminserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
	
	private final LogMapper logMapper;
	private Map<String, String> imgUrlMap = new HashMap<>();
	
	public List<LogDTO> findSlowByPage(){
		return logMapper.findSlowByPage();
	}
	
	public List<LogDTO> findErrorByPage(){
		return logMapper.findErrorByPage();
	}
	
	public List<LogDTO> findByTraceId(String traceId){
		return logMapper.findByTraceId(traceId);
	}

	public String buildPlantUML(String traceID, List<LogDTO> logList) {
		
		if (imgUrlMap.containsKey(traceID)) {
			return imgUrlMap.get(traceID);
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("@startuml").append(System.lineSeparator());
		
		for (int i = 0; i <= logList.size()-1; i++) {
			String addedString;
			
			if (i<=logList.size()-2) {
				addedString = logList.get(i).getCallerClass()+ " -> " + logList.get(i+1).getCallerClass() + " : " + logList.get(i).getCallerMethod();
			} else {
				addedString = logList.get(i).getCallerClass()+ " -> " + logList.get(0).getCallerClass() + " : " + logList.get(i).getCallerMethod();
			}
			
			
			
			builder.append(addedString).append(System.lineSeparator());
			
		}
		
		
		builder.append("@enduml");
		String url = builder.toString();
		
		imgUrlMap.put(traceID, url);
		
		System.out.println("made string : " + url);
		
		
		
		return null;
	}
}
