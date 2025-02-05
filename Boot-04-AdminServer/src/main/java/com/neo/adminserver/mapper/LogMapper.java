package com.neo.adminserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.neo.adminserver.dto.LogDTO;

@Mapper
public interface LogMapper {
	
	List<LogDTO> findByTraceId(String traceId);
	
	int countDelayLogs(LogDTO paramDto);

	List<LogDTO> findDelayLogs(@Param("paramDto") LogDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

    int countErrorLogs(LogDTO paramDto);

	List<LogDTO> findErrorLogs(@Param("paramDto") LogDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

	List<LogDTO> findInfluenceLogs(@Param("limit") int limit, @Param("offset") int offset, @Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);
	
	int countInfluenceLogs(@Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);

}
