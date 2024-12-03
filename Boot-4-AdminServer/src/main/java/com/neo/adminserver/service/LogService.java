package com.neo.adminserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {
	
	private final LogMapper mapper;
	
	public List<LogDTO> findSlowByPage(){
		return mapper.findSlowByPage();
	}
	
	public List<LogDTO> findErrorByPage(){
		return mapper.findErrorByPage();
	}
	
	public List<LogDTO> findByTraceId(){
		return mapper.findByTraceId();
	}
}
