package com.neo.adminserver.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.service.LogService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LogController {

	private final LogService logService;

	@GetMapping("/admin/slow")
    public String findSlowLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String uri,
            @RequestParam(required = false) String executeResult,
            Model model) {
		// 시간 변환
		// 실행 시간 조건 처리 (초 단위 → 밀리초 변환)
	    String convertExecuteResult = null;
	    if (executeResult != null && !executeResult.isEmpty()) {
	        convertExecuteResult = String.valueOf(Integer.parseInt(executeResult) * 1000);
	    }

	    // 검색 실행
	    List<LogDTO> logList = logService.findSlowLogs(page, size, startTime, endTime, traceId, userId, ipAddress, uri, convertExecuteResult);
	    int totalLogs = logService.countSlowSearchLogs(startTime, endTime, traceId, userId, ipAddress, uri, convertExecuteResult);
	    int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

	    // 결과 상태 및 모델 추가
	    boolean hasResults = !logList.isEmpty();
	    
        model.addAttribute("logList", logList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("traceId", traceId);
        model.addAttribute("userId", userId);
        model.addAttribute("ipAddress", ipAddress);
        model.addAttribute("uri", uri);
        model.addAttribute("executeResult", executeResult);
        model.addAttribute("hasResults", hasResults);
        return "slow_table";
    }
	
	@GetMapping("/admin/errors")
	public String findErrorByPage(
			@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String uri,
            Model model) {
        List<LogDTO> logList = logService.findErrorLogs(page, size, startTime, endTime, traceId, userId, ipAddress, query, uri);
        int totalLogs = logService.countErrorSearchLogs(startTime, endTime, traceId, userId, ipAddress, query, uri);
        int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);
        
        // 검색 결과 상태 추가
        boolean hasResults = !logList.isEmpty();

        model.addAttribute("logList", logList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("traceId", traceId);
        model.addAttribute("userId", userId);
        model.addAttribute("ipAddress", ipAddress);
        model.addAttribute("query", query);
        model.addAttribute("uri", uri);
        model.addAttribute("hasResults", hasResults);
        return "error_table";
	}
	
	@GetMapping("/admin/trace")
	public String findByTraceId(@RequestParam String traceId, Model model) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTraceId(traceId);
		model.addAttribute("logList", logList);
		String plantSource = logService.buildPlantUML(traceId, logList);
		model.addAttribute("imgSource", plantSource);
		return "trace_table";
	}
	
	@GetMapping("/admin/table")
	public String findByTable(
			@RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String callerMethod,
	        Model model) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTable(page, size, callerMethod);
		int totalLogs = logService.countSQLTable(callerMethod);
	    int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);
		
		// 검색 결과 상태 추가
        boolean hasResults = !logList.isEmpty();
        
        model.addAttribute("hasResults", hasResults);
		model.addAttribute("logList", logList);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("callerMethod", callerMethod);
		
		return "influence_table";
	}
}
