package com.neo.adminserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.neo.adminserver.dto.LogDTO;

@Mapper
public interface LogMapper {
	
	List<LogDTO> findSlowByPage(@Param("limit") int limit, @Param("offset") int offset);
	
	List<LogDTO> findErrorByPage(@Param("limit") int limit, @Param("offset") int offset);
	
	List<LogDTO> findByTraceId(String traceId);
	
	int countSlowLogs();
    int countErrorLogs();
	
	// logging_error 테이블
    List<LogDTO> findErrorLogsByTraceId(@Param("traceId") String traceId);
    List<LogDTO> findErrorLogsByUserId(@Param("userId") String userId);
    List<LogDTO> findErrorLogsByIpAddress(@Param("ipAddress") String ipAddress);
    List<LogDTO> findErrorLogsByURI(@Param("uri") String uri);
    List<LogDTO> findErrorLogsByQuery(@Param("query") String query);

    // logging_slow 테이블
    List<LogDTO> findSlowLogsByTraceId(@Param("traceId") String traceId);
    List<LogDTO> findSlowLogsByUserId(@Param("userId") String userId);
    List<LogDTO> findSlowLogsByIpAddress(@Param("ipAddress") String ipAddress);
    List<LogDTO> findSlowLogsByQuery(@Param("query") String query);
    List<LogDTO> findSlowLogsByURI(@Param("uri") String uri);

}
