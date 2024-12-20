package com.example.neobns.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParentBatch {

	private static final Logger logger = LoggerFactory.getLogger(ParentBatch.class);
	private final JobRepository jobRepository;
	private final Job subBatch1Job;
	private final Job subBatch2Job;
	private final JobLauncher jobLauncher;
	private final CustomBatchJobListener listener;

	public ParentBatch(JobRepository jobRepository, Job subBatch1Job, Job subBatch2Job, JobLauncher jobLauncher,
			CustomBatchJobListener listener) {
		this.jobRepository = jobRepository;
		this.subBatch1Job = subBatch1Job;
		this.subBatch2Job = subBatch2Job;
		this.jobLauncher = jobLauncher;
		this.listener = listener;
	}

	@Bean
	public Job parentBatchJob() {
		// 하위 배치 작업을 스텝을 통해 차례대로 실행
		return new JobBuilder("parentBatchJob", jobRepository).start(step1()).next(step2()).listener(listener).build();

	}

	@Bean
	public Step step1() {
		return (Step) new StepBuilder("step1", jobRepository).job(subBatch1Job).launcher(jobLauncher).build();
	}

	@Bean
	public Step step2() {
		return (Step) new StepBuilder("step2", jobRepository).job(subBatch2Job).launcher(jobLauncher).build();
	}

}
