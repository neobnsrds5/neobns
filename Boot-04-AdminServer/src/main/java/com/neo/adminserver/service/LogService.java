package com.neo.adminserver.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.data.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogMapper logMapper;

	public List<LogDTO> findSlowByPage(int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findSlowByPage(size, offset);
	}

	public List<LogDTO> findErrorByPage(int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findErrorByPage(size, offset);
	}

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}
	
	public int countSlowLogs() {
        return logMapper.countSlowLogs();
    }

    public int countErrorLogs() {
        return logMapper.countErrorLogs();
    }
    
    public List<LogDTO> findSlowLogs(
            int page,
            int size,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String uri,
            String executeResult) {
        int offset = (page - 1) * size;
        return logMapper.findSlowLogs(startTime, endTime, traceId, userId, ipAddress, uri, executeResult, size, offset);
    }

    public int countSlowSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String uri,
            String executeResult) {
        return logMapper.countSlowSearchLogs(startTime, endTime, traceId, userId, ipAddress, uri, executeResult);
    }
    
    public List<LogDTO> findErrorLogs(
            int page,
            int size,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query,
            String uri) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(startTime, endTime, traceId, userId, ipAddress, query, uri, size, offset);
    }

    public int countErrorSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query,
            String uri) {
        return logMapper.countErrorSearchLogs(startTime, endTime, traceId, userId, ipAddress, query, uri);
    }
    
	public List<LogDTO> findByTable(int page, int size, String searchType, String searchKeyword) {
		int offset = (page - 1) * size;
	    return logMapper.findByTable(size, offset, searchType, searchKeyword);
	}
	
    public int countSQLTable(String searchType, String searchKeyword) {
        return logMapper.countSQLTable(searchType, searchKeyword);
    }

}
