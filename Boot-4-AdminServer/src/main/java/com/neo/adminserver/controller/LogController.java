package com.neo.adminserver.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.service.LogService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LogController {

	private final LogService logService;

	@GetMapping("/slow")
	public String findSlowByPage(Model model) {
		List<LogDTO> logList = logService.findSlowByPage();
		model.addAttribute("logList", logList);
		return "slow_table";
	}
	
	@GetMapping("/errors")
	public String findErrorByPage(Model model) {
		List<LogDTO> logList = logService.findErrorByPage();	    
		model.addAttribute("logList", logList);
		return "error_table";
	}
	
	@GetMapping("/trace")
	public String findByTraceId(@RequestParam String traceId, Model model) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTraceId(traceId);
		model.addAttribute("logList", logList);
		String plantSource = logService.buildPlantUML(traceId, logList);
		model.addAttribute("imgSource", plantSource);
		return "trace_table";
	}
}
