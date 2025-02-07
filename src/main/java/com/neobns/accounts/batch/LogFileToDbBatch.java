package com.neobns.accounts.batch;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.neobns.accounts.batch.service.LogFileToDbService;


@Configuration
public class LogFileToDbBatch {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private LogFileToDbService logFileToDbService;
	private String path = "../logs/application/rolling";

	public LogFileToDbBatch(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			LogFileToDbService logFileToDbService) {
		super();
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.logFileToDbService = logFileToDbService;
	}

	@Bean
	public Job logToDBJob() {
		return new JobBuilder("logToDBJob", jobRepository).start(logToDBStep()).build();
	}

	// tasklet으로 메서드 호출
	@Bean
	public Step logToDBStep() {

		return new StepBuilder("logToDBStep", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				// 롤링된 파일
				File folder = new File(path);
				File[] files = folder.listFiles();

				int count = 0;

				if (files != null) {
					for (File file : files) {
						if (file.isFile()) {
							count++;
							logFileToDbService.executeLogFileToDb(file.getAbsolutePath());
						}
						// 적재 후 파일 삭제
						file.delete();
					}
				}

				return RepeatStatus.FINISHED;
			}
		}).transactionManager(transactionManager).build();
	}

}
