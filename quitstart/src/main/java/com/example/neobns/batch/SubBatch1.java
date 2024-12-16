package com.example.neobns.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SubBatch1 {

	private static final Logger logger = LoggerFactory.getLogger(SubBatch1.class);
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final CustomBatchJobListener listener;

	public SubBatch1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
			CustomBatchJobListener listener) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
		this.listener = listener;
	}

	@Bean
	public Job subBatch1Job() {
		return new JobBuilder("subBatch1", jobRepository).start(subBatch1Step()).listener(listener).build();

	}

	@Bean
	public Step subBatch1Step() {

		return (Step) new StepBuilder("subBatch1Step", jobRepository).tasklet((stepContribution, chunkContext) -> {
			logger.info("Sub Batch1 실행 중");
			logger.debug("Sub Batch1 실행 중");
			Thread.sleep(1000);
			logger.info("Sub Batch1 실행 완료");
			logger.debug("Sub Batch1 실행 완료");
			return null;
		}, platformTransactionManager).build();

	}

}
