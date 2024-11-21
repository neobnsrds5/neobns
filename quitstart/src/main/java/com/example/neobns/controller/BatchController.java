package com.example.neobns.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BatchController {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	
	@GetMapping("/batch/first")
	public String firstApi(@RequestParam("value") String value) throws Exception{
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("date", value)
				.toJobParameters();
		
		jobLauncher.run(jobRegistry.getJob("toRestJob"), jobParameters);
		return "OK";
	}
	
}
