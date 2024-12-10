package com.example.neobns.batch;

import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import com.example.neobns.service.BatchHistoryService;

@Component
public class CustomBatchJobListener{

	private final BatchHistoryService service;

	public CustomBatchJobListener(BatchHistoryService service) {
		super();
		this.service = service;
	}
	
	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		MDC.put("batchAppId", "com.example.neobns.batch." + jobName);
        MDC.put("instanceId", jobExecution.getId().toString());
    }

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		service.saveBatchHistory(jobExecution);
	}

}
