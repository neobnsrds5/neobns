package neo.spider.solution.batch.batchJobs;

import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.stereotype.Component;

import neo.spider.solution.batch.service.BatchHistoryService;

@Component // 현재 스파이더 FWK_ERROR_HIS 테이블 없어 배치 Job에 리스너 추가하지 않음
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
		MDC.put("batchAppId", "neo.spider.solution.batch." + jobName);
        MDC.put("instanceId", jobExecution.getId().toString());
		service.saveBatchHistory(jobExecution);
		MDC.remove("batchAppId");
		MDC.remove("instanceId");
	}

}
