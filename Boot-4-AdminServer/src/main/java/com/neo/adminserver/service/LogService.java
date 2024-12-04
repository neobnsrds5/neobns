package com.neo.adminserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
	
	private final LogMapper logMapper;
	
	public List<LogDTO> findSlowByPage(){
		return logMapper.findSlowByPage();
	}
	
	public List<LogDTO> findErrorByPage(){
		return logMapper.findErrorByPage();
	}
	
	public List<LogDTO> findByTraceId(){
		return logMapper.findByTraceId();
	}
}
