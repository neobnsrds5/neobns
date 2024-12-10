package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {
	
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	
	@GetMapping("/dbapi")
	public String dbapi() throws Exception {
		String uniqVal = String.valueOf(System.currentTimeMillis()) ;

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtoapi", uniqVal).toJobParameters();

		jobLauncher.run(jobRegistry.getJob("dbToApiJob"), jobParameters);
		return uniqVal;
	}
	
	@GetMapping("/dbdb")
	public String dbdb() throws Exception{
		String uniqVal = String.valueOf(System.currentTimeMillis()) ;

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtodb", uniqVal).toJobParameters();

		jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);
		return uniqVal;
	}
	
	@GetMapping("/logdb")
	public String logdb() throws Exception{
		String uniqVal = String.valueOf(System.currentTimeMillis()) ;

		JobParameters jobParameters = new JobParametersBuilder().addString("logtodb", uniqVal).toJobParameters();

		jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);
		return uniqVal;
	}
	
}
