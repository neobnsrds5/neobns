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

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}
	
	public int countSlowLogs(LogDTO paramDto) {
        return logMapper.countSlowLogs(paramDto);
    }

    public List<LogDTO> findSlowLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findSlowLogs(paramDto, size, offset);
    }

    public int countErrorLogs(LogDTO paramDto) {
        // 초 단위를 밀리초 단위로 변경
        String executeResult = paramDto.getExecuteResult();
        if(executeResult != null && !executeResult.isEmpty()){
            paramDto.setExecuteResult(String.valueOf(Integer.parseInt(executeResult) * 1000));
        }
        return logMapper.countErrorLogs(paramDto);
    }
    
    public List<LogDTO> findErrorLogs(LogDTO paramDto, int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(paramDto, size, offset);
    }
    
	public List<LogDTO> findByTable(int page, int size, String searchType, String searchKeyword) {
		int offset = (page - 1) * size;
	    return logMapper.findByTable(size, offset, searchType, searchKeyword);
	}
	
    public int countSQLTable(String searchType, String searchKeyword) {
        return logMapper.countSQLTable(searchType, searchKeyword);
    }

}
