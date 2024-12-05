package com.neo.adminserver.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
		return "slow";
	}
	
	@GetMapping("/errors")
	public String findErrorByPage(Model model) {
		List<LogDTO> logList = logService.findErrorByPage();	    
		model.addAttribute("logList", logList);
		return "error";
	}
	
	@GetMapping("/trace/{traceId}")
	public String findByTraceId(@PathVariable String traceId, Model model) {
		List<LogDTO> logList = logService.findByTraceId(traceId);
		model.addAttribute("logList", logList);
		return "trace";
	}
}
