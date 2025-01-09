package com.neo.adminserver.controller;

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
import com.neo.adminserver.dto.BatchJobExecutionDTO;
import com.neo.adminserver.dto.BatchJobInstanceDTO;
import com.neo.adminserver.dto.BatchStepExecutionDTO;
import com.neo.adminserver.service.BatchService;
import com.neo.adminserver.util.DateUtil;
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
		
		String todayDate = DateUtil.getDate("yyyy-MM-dd");
		
	    logger.info("----------------------------------------------------");
	    logger.info("/batch/jobList");
	    logger.info("paramMap : " + paramMap);   
	    logger.info("----------------------------------------------------");
	        
	    //------------------------------------------------------------------------
	    // SearchMap Init
	    //------------------------------------------------------------------------
	    SearchMap searchMap = new SearchMap(paramMap);
	    searchMap.initParam("status", "COMPLETED");
	    searchMap.initParam("jobName", "");
	    searchMap.initParam("startDate", todayDate);
	    searchMap.initParam("endDate", todayDate);
	    
	    //------------------------------------------------------------------------
	    // pageLink init
	    //------------------------------------------------------------------------
	    int total = batchService.listCount(searchMap);
	    pgtl.init(total, "/batch/jobList", searchMap.getParams());
	    searchMap.setPgtl(pgtl);
	    
		List<BatchJobInstanceDTO> list = batchService.list(searchMap);

		String[] status = {"COMPLETED", "STARTING", "STARTED", "STOPPING", "STOPPED", "FAILED", "UNKNOWN"};
		
		for(int i=0; i<total; i++) {
		    logger.info("JobInstance : {}", list.get(i).toString());
		}
		
		model.addAttribute("list", list);
		model.addAttribute("searchMap", searchMap);
		model.addAttribute("statusList", status);
		return "batch/jobList";		
	}
	
	@GetMapping("/jobDetail")
	public String jobDetail(Model model, BatchJobInstanceDTO paramVo) {
		
		BatchJobInstanceDTO job = null;
		List<BatchStepExecutionDTO> steps = null;
		
		job = batchService.selectJobDetail(paramVo.getInstanceId());
		
		logger.info("BatchJobInstanceEntity : " + job.toString());
		
		if(job.getExec() != null) {
			steps = batchService.listStepDetail(job.getExec().getExecutionId());
		}
		
		model.addAttribute("job", job);
		model.addAttribute("steps", steps);
		
		return "batch/jobDetail";		
	}	
	
	@GetMapping("/stepDetail")
	public String stepDetail(Model model, BatchJobExecutionDTO paramVo) {
		
		BatchJobExecutionDTO job = batchService.selectStepDetail(paramVo.getExecutionId());
		
		logger.info("BatchJobExecutionEntity : " + job.toString());
		
		model.addAttribute("job", job);
		return "batch/stepDetail";		
	}	

}