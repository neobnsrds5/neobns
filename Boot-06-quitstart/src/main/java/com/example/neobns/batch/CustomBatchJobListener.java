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
	
	// 배치 잡 이름, 아이디를 히스토리 내역 테이블에 저장
	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		MDC.put("batchAppId", "com.example.neobns.batch." + jobName);
        MDC.put("instanceId", jobExecution.getId().toString());
		service.saveBatchHistory(jobExecution);
		MDC.remove("batchAppId");
		MDC.remove("instanceId");
	}

}
