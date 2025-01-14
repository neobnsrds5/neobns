package com.neobns.accounts.batch;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.neobns.accounts.dto.LogDTO;

@Configuration
public class LogFileToDbBatch {

	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public LogFileToDbBatch(@Qualifier("dataDataSource")  DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	@Bean
	public Job logToDBJob() {
		return new JobBuilder("logToDBJob", jobRepository).start(logToDBStep()).build();
	}

	@Bean
	public Step logToDBStep() {

		int chunkSize = 500; // 10, 50, 100

		return new StepBuilder("logToDBStep", jobRepository).<LogDTO, LogDTO>chunk(chunkSize, transactionManager)
				.reader(logReader()).processor(dummyProcessor3()).writer(compositeWriter())
				.taskExecutor(logToDBTaskExecutor()).build();
	}

	@Bean
	public ItemProcessor<LogDTO, LogDTO> dummyProcessor3() {
		return new ItemProcessor<LogDTO, LogDTO>() {

			@Override
			public LogDTO process(LogDTO item) throws Exception {
				String threadName = Thread.currentThread().getName();
				return item;
			}
		};
	}

	@Bean
	public TaskExecutor logToDBTaskExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("logToDBTask");
		executor.initialize();
		return executor;
	}

	@Bean
	public FlatFileItemReader<LogDTO> logReader() {
		FlatFileItemReader<LogDTO> reader = new FlatFileItemReader<>();
		String path = "../logs/accounts-application.log";
		reader.setResource(new FileSystemResource(path));

		DefaultLineMapper<LogDTO> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(";");
		tokenizer.setNames("timestmp", "loggerName", "levelString", "callerClass", "callerMethod", "traceId", "userId",
				"ipAddress", "device", "executeResult", "query", "uri");
		lineMapper.setLineTokenizer(tokenizer);

		BeanWrapperFieldSetMapper<LogDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(LogDTO.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		reader.setLineMapper(lineMapper);
		return reader;
	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingEventWriter() {
		String sql = "INSERT INTO logging_event (timestmp, logger_name, level_string, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result) "
				+ "VALUES (:timestmp, :loggerName, :levelString, :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device, :executeResult)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();
	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingSlowWriter() {
		String sql = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, query, uri) "
				+ "VALUES (:timestmp, :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device,"
				+ ":executeResult, "
				+ "CASE WHEN :callerClass = 'SQL' THEN :callerMethod ELSE NULL END, CASE WHEN :callerClass != 'SQL' THEN :callerClass ELSE NULL END)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();
	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingErrorWriter() {
		String sql = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query, uri, execute_result) "
				+ "VALUES (:timestmp, :userId, :traceId, :ipAddress, :device, :callerClass, :callerMethod, "
				+ ":query, :uri, :executeResult)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();
	}

	@Bean
	public CompositeItemWriter<LogDTO> compositeWriter() {
		CompositeItemWriter<LogDTO> writer = new CompositeItemWriter<>();
		writer.setDelegates(List.of(conditionalEventWriter(), conditionalErrorWriter(), conditionalSlowWriter()));
		return writer;
	}

	@Bean
	public ItemWriter<LogDTO> conditionalEventWriter() {
		return items -> {
			for (LogDTO log : items) {
				if ("trace".equalsIgnoreCase(log.getLoggerName()) || "slow".equalsIgnoreCase(log.getLoggerName()) || "error".equalsIgnoreCase(log.getLoggerName())) {
					loggingEventWriter().write(new Chunk<>(List.of(log)));
				}
			}
		};
	}
	
	@Bean
	public ItemWriter<LogDTO> conditionalSlowWriter() {
		return items -> {
			for (LogDTO log : items) {
				if ("slow".equalsIgnoreCase(log.getLoggerName())) {
					loggingSlowWriter().write(new Chunk<>(List.of(log)));
				}
			}
		};
	}

	@Bean
	public ItemWriter<LogDTO> conditionalErrorWriter() {
		return items -> {
			for (LogDTO log : items) {
				if ("error".equalsIgnoreCase(log.getLoggerName())) {
					try {
						loggingErrorWriter().write(new Chunk<>(List.of(log)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

}
