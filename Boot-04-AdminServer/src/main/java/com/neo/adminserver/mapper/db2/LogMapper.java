package com.neo.adminserver.mapper.db2;

import java.time.LocalDateTime;
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
	
    List<LogDTO> findSlowLogs(
    		@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("traceId") String traceId,
            @Param("userId") String userId,
            @Param("ipAddress") String ipAddress,
            @Param("uri") String uri,
            @Param("executeResult") String executeResult,
            @Param("limit") int limit,
            @Param("offset") int offset
        );
    
    int countSlowSearchLogs(
    		@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("traceId") String traceId,
            @Param("userId") String userId,
            @Param("ipAddress") String ipAddress,
            @Param("uri") String uri,
            @Param("executeResult") String executeResult
        );
    
    List<LogDTO> findErrorLogs(
    		@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("traceId") String traceId,
            @Param("userId") String userId,
            @Param("ipAddress") String ipAddress,
            @Param("query") String query,
            @Param("uri") String uri,
            @Param("limit") int limit,
            @Param("offset") int offset
        );
    
    int countErrorSearchLogs(
    		@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("traceId") String traceId,
            @Param("userId") String userId,
            @Param("ipAddress") String ipAddress,
            @Param("query") String query,
            @Param("uri") String uri
        );

	List<LogDTO> findByTable(@Param("limit") int limit, @Param("offset") int offset, @Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);
	
	int countSQLTable(@Param("searchType") String searchType, @Param("searchKeyword") String searchKeyword);

}
