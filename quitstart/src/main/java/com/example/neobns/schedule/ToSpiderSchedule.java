package com.example.neobns.schedule;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

		System.out.println("runSpiderSchedule 실행 ");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
		String date = LocalDateTime.now().format(formatter);

		JobParameters jobParameters = new JobParametersBuilder().addString("date", date)
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

		jobLauncher.run(jobRegistry.getJob("dbToSpiderErrorJob"), jobParameters);

		System.out.println("runSpiderSchedule() : 실행됨 ");
	}

}
