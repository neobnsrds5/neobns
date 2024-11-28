package com.example.neobns.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BatchController {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	
	@GetMapping("/batch/dbtoapi/{value}")
	public String firstApi(@PathVariable("value") String value) throws Exception{
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("dbtoapi", value)
				.toJobParameters();
		
		jobLauncher.run(jobRegistry.getJob("dbToApiJob"), jobParameters);
		return "OK";
	}
	
	@GetMapping("/batch/dbtodb/{value}")
	public String dbtodbTest(@PathVariable("value") String value) throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("dbtodb", value)
				.toJobParameters();
		jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);
		return "OK";
	}
	
	@GetMapping("/batch/filetodb/{value}")
	public String fileToDbTest(@PathVariable("value") String value) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("filetodb", value)
				.toJobParameters();
		
		try {
			jobLauncher.run(jobRegistry.getJob("fileToDBJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}
	
	@GetMapping("/batch/logtodb/{value}")
	public String logToDbTest(@PathVariable("value") String value) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("logtodb", value)
				.toJobParameters();
		
		try {
			jobLauncher.run(jobRegistry.getJob("logToDBJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}
	
}