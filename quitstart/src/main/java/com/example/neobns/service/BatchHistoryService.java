package com.example.neobns.service;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class BatchHistoryService {

	private final JdbcTemplate jdbcTemplate;

	public BatchHistoryService(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void saveBatchHistory(JobExecution jobExecution) {
		
		String jobName = jobExecution.getJobInstance().getJobName();
		String status = jobExecution.getStatus().toString();
		LocalDateTime startTime = jobExecution.getStartTime();
		LocalDateTime endTime = jobExecution.getEndTime();
		String exitCode = jobExecution.getExitStatus().getExitCode();
		String exitMessage = jobExecution.getExitStatus().getExitDescription();
		
		String jobSql = "INSERT INTO FWK_BATCH_HIS(job_name, status, start_time, end_time, exit_code, exit_message, create_time)" + "VALUES (?,?,?,?,?,?,NOW())";
		jdbcTemplate.update(jobSql, jobName, status, startTime, endTime, exitCode, exitMessage);
		
	}

}
