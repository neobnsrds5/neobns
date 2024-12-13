package com.example.neobns.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.transaction.Transactional;

@Service
public class BatchHistoryService {

	private final JdbcTemplate spiderTemplate;
	private final JobRegistry jobRegistry;

	public BatchHistoryService(JdbcTemplate spiderTemplate, JobRegistry jobRegistry) {
		super();
		this.spiderTemplate = spiderTemplate;
		this.jobRegistry = jobRegistry;
	}

	public void saveBatchHistory(JobExecution jobExecution) {

		int maxLen = 3000;

		String batchAppId = MDC.get("batchAppId");
		String instanceId = MDC.get("instanceId");
		String batchDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//		int batchExecuteSeq = 12;
		String logDtime = jobExecution.getStartTime() != null
				? jobExecution.getStartTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				: "2024-01-01";
		String batchEndDtime = jobExecution.getEndTime() != null
				? jobExecution.getEndTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				: logDtime;
		String resRtCode = jobExecution.getStatus().isUnsuccessful() ? "1" : "0";
		String lastUpdateUserId = MDC.get("userId") != null ? MDC.get("userId") : "Missed-ID";
		String errorCode = jobExecution.getExitStatus().getExitCode().equals("COMPLETED") ? "정상" : "비정상";
		String errorReason = jobExecution.getExitStatus().getExitDescription().length() > maxLen
				? jobExecution.getExitStatus().getExitDescription().substring(0, maxLen)
				: jobExecution.getExitStatus().getExitDescription();
		Integer recordCount = (int) jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount)
				.sum();
		Integer failCount = (int) jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteSkipCount)
				.sum();
		Integer executeCount = recordCount + failCount;
		Integer successCount = recordCount;

		String jobSql = "INSERT INTO FWK_BATCH_HIS (" + "BATCH_APP_ID, INSTANCE_ID, BATCH_DATE, LOG_DTIME, "
				+ "BATCH_END_DTIME, RES_RT_CODE, LAST_UPDATE_USER_ID, ERROR_CODE, ERROR_REASON, "
				+ "RECORD_COUNT, EXECUTE_COUNT, SUCCESS_COUNT, FAIL_COUNT) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		spiderTemplate.update(jobSql, batchAppId, instanceId, batchDate, logDtime, batchEndDtime, resRtCode,
				lastUpdateUserId, errorCode, errorReason, recordCount, executeCount, successCount, failCount);

	}

}
