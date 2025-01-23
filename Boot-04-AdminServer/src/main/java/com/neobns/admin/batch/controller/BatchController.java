package com.neobns.admin.batch.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	public String list(Model model, @RequestParam HashMap<String, Object> paramMap, PageUtil pgtl) {

		logger.info("----------------------------------------------------");
		logger.info("/batch/jobList");
		logger.info("paramMap : " + paramMap);
		logger.info("----------------------------------------------------");

		// ------------------------------------------------------------------------
		// SearchMap Init
		// ------------------------------------------------------------------------
		SearchMap searchMap = new SearchMap(paramMap);
		searchMap.initParam("status", "");
		searchMap.initParam("jobName", "");
		searchMap.initParam("startDate", "");
		searchMap.initParam("endDate", "");

		// ------------------------------------------------------------------------
		// pageLink init
		// ------------------------------------------------------------------------
		int total = batchService.countJobs(searchMap);
		pgtl.init(total, "/batch/jobList", searchMap.getParams());
		searchMap.setPgtl(pgtl);

		List<BatchJobInstanceDTO> list = batchService.findJobs(searchMap);

		String[] status = { "COMPLETED", "STARTING", "STARTED", "STOPPING", "STOPPED", "FAILED", "UNKNOWN" };

		model.addAttribute("list", list);
		model.addAttribute("searchMap", searchMap);
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