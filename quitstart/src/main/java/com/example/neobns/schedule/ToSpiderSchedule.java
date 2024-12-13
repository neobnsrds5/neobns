package com.example.neobns.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ToSpiderSchedule {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	
	@Scheduled(cron = "0 */1 * * * * ", zone = "Asia/Seoul")
	public void runSpiderSchedule() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String date = dateFormat.format(new Date());
		
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("date", date)
				.toJobParameters();
		
		jobLauncher.run(jobRegistry.getJob("dbToSpiderErrorJobs"), jobParameters);
	}
	
}
