//package com.example.neobns.controller;
//
//import java.util.List;
//
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.configuration.JobRegistry;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.example.neobns.dto.FwkErrorHisDto;
//import com.example.neobns.oraclemapper.OracleMapper;
//import com.example.neobns.service.RestCallAccountsService;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//public class ShowErrorController {
//	
//	private final OracleMapper mapper;
//	private final RestCallAccountsService rsService;
//	private final JobLauncher jobLauncher;
//	private final JobRegistry jobRegistry;
//	
//	@GetMapping("/alertError")
//	public String index() {
//		return "index2";
//	}
//	
//	@GetMapping("/showErrorInOracle")
//	@ResponseBody
//	public List<FwkErrorHisDto> getErrors(){
//		return mapper.getRecords(0L);
//	}
//	
//	@GetMapping("/testQuery/{val}")
//	@ResponseBody
//	public List<FwkErrorHisDto> getSum(@PathVariable("val") long val){
//		return mapper.getRecords(val);
//	}
//	
//	@GetMapping("/makeErrors")
//	@ResponseBody
//	public void makeErrors() {
//		for (int i = 0 ; i < 3 ; i++) {
//			rsService.initiateESql(1234L);
//		}
//		String uniqVal = "1" + System.currentTimeMillis();
//		
//		JobParameters jobParameters = new JobParametersBuilder().addString("spiderBatch", uniqVal).toJobParameters();
//		
//		try {
//			jobLauncher.run(jobRegistry.getJob("dbToSpiderErrorJob"), jobParameters);
//		} catch (Exception e) {
//		}
//	}
//
//}
