package com.neo.adminserver.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.service.LogService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LogController {
	
	private final LogService service;

	@GetMapping("/slow")
	public String findSlowByPage(Model model) {
		List<LogDTO> logList = service.findSlowByPage();
		model.addAttribute("logList", logList);
		return "slow";
	}
}
