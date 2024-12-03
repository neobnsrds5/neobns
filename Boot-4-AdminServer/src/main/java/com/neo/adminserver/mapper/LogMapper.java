package com.neo.adminserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.neo.adminserver.dto.LogDTO;

@Mapper
public interface LogMapper {
	
	List<LogDTO> findSlowByPage();
	
	List<LogDTO> findErrorByPage();
	
	List<LogDTO> findByTraceId();
}
