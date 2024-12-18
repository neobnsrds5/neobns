package com.example.neobns;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileToDbBatchTest {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JobRegistry jobRegistry;

	@Test
	public void fileToDb() throws Exception {
		System.out.println("===============================file to db 시작==================================");
		MDC.put("userId", "testBatch");
		String uniqVal = String.valueOf(System.currentTimeMillis());
		JobParameters jobParameters = new JobParametersBuilder().addString("fileToDBJob", uniqVal).toJobParameters();
		jobLauncher.run(jobRegistry.getJob("fileToDBJob"), jobParameters);
		MDC.remove("userId");
		System.out.println("================================file to db 끝=================================");
	}

}
