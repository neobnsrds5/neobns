package com.neo.adminserver.controller; // 패키지명 변경 com.neobns.admin.e2e.controller

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.service.LogService;
import com.neo.adminserver.service.TraceUmlService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LogController {

	private final LogService logService;

	@GetMapping("/admin/slow") // uri 자세하게 변경 /admin/e2e/slow
    public String findSlowLogs(
			Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		// 초 단위를 밀리 초 단위로 변경
		String executeResult = paramDto.getExecuteResult();
        if(executeResult != null && !executeResult.isEmpty()){
            paramDto.setExecuteResult(String.valueOf(Integer.parseInt(executeResult) * 1000));
        }

		List<LogDTO> logList = logService.findSlowLogs(paramDto, page, size);
		int totalLogs = logService.countSlowLogs(paramDto);
		int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "slow_table";
    }
	
	@GetMapping("/admin/errors")
	public String findErrorByPage(
			Model model,
			@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		// 초 단위를 밀리 초 단위로 변경
		String executeResult = paramDto.getExecuteResult();
		if(executeResult != null && !executeResult.isEmpty()){
			paramDto.setExecuteResult(String.valueOf(Integer.parseInt(executeResult) * 1000));
		}

        List<LogDTO> logList = logService.findErrorLogs(paramDto, page, size);
        int totalLogs = logService.countErrorLogs(paramDto);
        int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "error_table";
	}
	
	@GetMapping("/admin/trace")
	public String findByTraceId(Model model, @RequestParam String traceId) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTraceId(traceId);
		model.addAttribute("logList", logList);
		model.addAttribute("imgSource", TraceUmlService.buildUmlList(logList));
		return "trace_table";
	}
	
	@GetMapping("/admin/table")
	public String findByTable(
			Model model,
			@RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {
		List<LogDTO> logList = logService.findByTable(page, size, searchType, searchKeyword);
		int totalLogs = logService.countSQLTable(searchType, searchKeyword);
	    int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

		model.addAttribute("logList", logList);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("searchType" , searchType);
	    model.addAttribute("searchKeyword", searchKeyword);
		
		return "influence_table";
	}
}
