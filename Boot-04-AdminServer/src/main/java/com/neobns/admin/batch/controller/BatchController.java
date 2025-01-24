package com.neobns.admin.batch.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neo.adminserver.common.SearchMap;
import com.neobns.admin.batch.dto.BatchJobExecutionDTO;
import com.neobns.admin.batch.dto.BatchJobInstanceDTO;
import com.neobns.admin.batch.dto.BatchStepExecutionDTO;
import com.neobns.admin.batch.service.BatchService;
import com.neo.adminserver.util.PageUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {

	private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

	private final BatchService batchService;

	@GetMapping("/jobList")
	public String list(Model model,
					   @RequestParam(defaultValue = "1") int page,
					   @RequestParam(defaultValue = "10") int size,
					   @ModelAttribute BatchJobInstanceDTO paramDto) {
		logger.info("{}", size);
		List<BatchJobInstanceDTO> jobList = batchService.findJobs(paramDto, page, size);
		logger.info("jobList size : {}", jobList.size());
		int totalJobs = batchService.countJobs(paramDto);
		logger.info("totalJobs : {}", totalJobs);
		int totalPages = totalJobs == 0 ? 0 : (int) Math.ceil((double) totalJobs / size);
		String[] status = { "COMPLETED", "STARTING", "STARTED", "STOPPING", "STOPPED", "FAILED", "UNKNOWN" };

		model.addAttribute("jobList", jobList);
		model.addAttribute("param", paramDto);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("statusList", status);

		return "batch/jobList";
	}

	@GetMapping("/jobDetail")
	public String jobDetail(Model model, BatchJobInstanceDTO paramVo) {

		BatchJobInstanceDTO job = null;
		List<BatchStepExecutionDTO> steps = null;

		job = batchService.findJobById(paramVo.getInstanceId());

		logger.info("BatchJobInstanceEntity : " + job.toString());

		if (job.getExec() != null) {
			steps = batchService.findStepsByJobId(job.getExec().getExecutionId());
		}

		model.addAttribute("job", job);
		model.addAttribute("steps", steps);

		return "batch/jobDetail";
	}

	@GetMapping("/stepDetail")
	public String stepDetail(Model model, BatchJobExecutionDTO paramVo) {

		BatchJobExecutionDTO job = batchService.findStepById(paramVo.getExecutionId());

		logger.info("BatchJobExecutionEntity : " + job.toString());

		model.addAttribute("job", job);
		return "batch/stepDetail";
	}

}