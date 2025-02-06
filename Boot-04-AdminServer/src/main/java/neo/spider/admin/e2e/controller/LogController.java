package neo.spider.admin.e2e.controller;

import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import neo.spider.admin.e2e.dto.LogDTO;
import neo.spider.admin.e2e.service.LogService;
import neo.spider.admin.e2e.service.TraceUmlService;

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
		if (paramDto.getStartTime() != null) {
			paramDto.setLtCallTimestamp(
					paramDto.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		if (paramDto.getEndTime() != null) {
			paramDto.setGtCallTimestamp(
					paramDto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}

		List<LogDTO> logList = logService.findDelayLogs(paramDto, page, size);
		int totalLogs = logService.countDelayLogs(paramDto);
		int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "e2e/slow_table";
    }
	
	@GetMapping("/admin/errors")
	public String findErrorByPage(
			Model model,
			@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute LogDTO paramDto) {

		// 초 단위를 밀리 초 단위로 변경
		String executeResult = paramDto.getExecuteResult();
		if (executeResult != null && !executeResult.isEmpty()) {
			paramDto.setExecuteResult(String.valueOf(Integer.parseInt(executeResult) * 1000));
			paramDto.setSearchExecuteTime(Long.parseLong(executeResult) * 1000);
		}
		if (paramDto.getStartTime() != null) {
			paramDto.setLtCallTimestamp(
					paramDto.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
		if (paramDto.getEndTime() != null) {
			paramDto.setGtCallTimestamp(
					paramDto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}

        List<LogDTO> logList = logService.findErrorLogs(paramDto, page, size);
        int totalLogs = logService.countErrorLogs(paramDto);
        int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

        model.addAttribute("logList", logList);
		model.addAttribute("param", paramDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "e2e/error_table";
	}
	
	@GetMapping("/admin/trace")
	public String findByTraceId(Model model, @RequestParam String traceId) throws CloneNotSupportedException {
		List<LogDTO> logList = logService.findByTraceId(traceId);

		model.addAttribute("logList", logList);
		model.addAttribute("imgSource", TraceUmlService.buildUmlList(logList));
		return "e2e/trace_table";
	}
	
	@GetMapping("/admin/table")
	public String findByTable(
			Model model,
			@RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword) {
		List<LogDTO> logList = logService.findInfluenceLogs(page, size, searchType, searchKeyword);
		int totalLogs = logService.countInfluenceLogs(searchType, searchKeyword);
	    int totalPages = totalLogs == 0 ? 0 : (int) Math.ceil((double) totalLogs / size);

		model.addAttribute("logList", logList);
		model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("searchType" , searchType);
	    model.addAttribute("searchKeyword", searchKeyword);
		
		return "e2e/influence_table";
	}
}
