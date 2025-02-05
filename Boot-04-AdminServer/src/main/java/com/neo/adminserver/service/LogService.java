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

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}
	
	public int countDelayLogs(LogDTO paramDto) {
        return logMapper.countDelayLogs(paramDto);
    }

    public List<LogDTO> findDelayLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findDelayLogs(paramDto, size, offset);
    }

    public int countErrorLogs(LogDTO paramDto) {
        return logMapper.countErrorLogs(paramDto);
    }
    
    public List<LogDTO> findErrorLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(paramDto, size, offset);
    }
    
	public List<LogDTO> findInfluenceLogs(int page, int size, String searchType, String searchKeyword) {
		int offset = (page - 1) * size;
	    return logMapper.findInfluenceLogs(size, offset, searchType, searchKeyword);
	}
	
    public int countInfluenceLogs(String searchType, String searchKeyword) {
        return logMapper.countInfluenceLogs(searchType, searchKeyword);
    }

}
