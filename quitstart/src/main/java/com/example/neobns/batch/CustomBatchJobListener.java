package com.example.neobns.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;

import com.example.neobns.service.BatchHistoryService;

public class CustomBatchJobListener{

	private final BatchHistoryService service;

	public CustomBatchJobListener(BatchHistoryService service) {
		super();
		this.service = service;
	}

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		service.saveBatchHistory(jobExecution);
	}

}
