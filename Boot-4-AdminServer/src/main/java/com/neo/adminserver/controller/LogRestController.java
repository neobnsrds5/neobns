package com.neo.adminserver.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.service.LogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogRestController {
	
	private final LogService logService;
	
	@GetMapping("/error/search")
    public List<LogDTO> searchErrorLogs(
            @RequestParam String criteria,
            @RequestParam String value) {
        return logService.searchLogs(criteria, value, "logging_error");
    }

    @GetMapping("/slow/search")
    public List<LogDTO> searchSlowLogs(
            @RequestParam String criteria,
            @RequestParam String value) {
        return logService.searchLogs(criteria, value, "logging_slow");
    }

}
