package com.example.neobns.batch;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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

import com.example.neobns.dto.LogDTO;

@Configuration
public class LogToDbBatch {

	private final DataSource datasource;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public LogToDbBatch(@Qualifier("dataDBSource") DataSource datasource, JobRepository jobRepository,
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
		return new StepBuilder("logToDBStep", jobRepository).<LogDTO, LogDTO>chunk(100, transactionManager)
				.reader(logReader())
				.writer(compositeWriter())
				.taskExecutor(logToDBTaskExecutor())
				.build();
	}

	@Bean
	public TaskExecutor logToDBTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("logToDBTask");
		executor.initialize();
		return executor;
	}

	@Bean
	public FlatFileItemReader<LogDTO> logReader() {
		FlatFileItemReader<LogDTO> reader = new FlatFileItemReader<>();
		String path = "../logs/application.log";
		reader.setResource(new FileSystemResource(path));

		DefaultLineMapper<LogDTO> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(";");
		tokenizer.setNames("timestmp", "loggerName", "levelString", "callerClass", "callerMethod", "traceId", "userId", "ipAddress", "device", "executeResult");
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
				+ "VALUES (UNIX_TIMESTAMP(:timestmp), :loggerName, :levelString, :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device, "
				+ "CASE WHEN :executeResult = '' OR :executeResult IS NULL THEN NULL ELSE CAST(:executeResult AS UNSIGNED) END)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();
	}
	
	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingSlowWriter() {
	    String sql = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, query, uri) "
	    		+ "VALUES (UNIX_TIMESTAMP(:timestmp), :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device,"
	    		+ "CASE WHEN :executeResult = '' OR :executeResult IS NULL THEN NULL ELSE CAST(:executeResult AS UNSIGNED) END, "
	    		+ "CASE WHEN :callerClass = 'SQL' THEN :callerMethod ELSE NULL END, CASE WHEN :callerClass != 'SQL' THEN :callerClass ELSE NULL END)";

	    return new JdbcBatchItemWriterBuilder<LogDTO>()
	            .dataSource(datasource)
	            .sql(sql)
	            .beanMapped()
	            .build();
	}
	
	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingErrorWriter() {
		String sql = "INSERT INTO logging_error (timestmp, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, query, uri) "
	    		+ "VALUES (UNIX_TIMESTAMP(:timestmp), :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device,"
	    		+ "CASE WHEN :executeTime = '' OR :executeTime IS NULL THEN NULL ELSE CAST(:executeTime AS UNSIGNED) END, "
	    		+ "CASE WHEN :callerClass = 'SQL' THEN :callerMethod ELSE NULL END, CASE WHEN :callerClass != 'SQL' THEN :callerClass ELSE NULL END)";

	    return new JdbcBatchItemWriterBuilder<LogDTO>()
	            .dataSource(datasource)
	            .sql(sql)
	            .beanMapped()
	            .build();
	}
	
	@Bean
	public CompositeItemWriter<LogDTO> compositeWriter() {
	    CompositeItemWriter<LogDTO> writer = new CompositeItemWriter<>();
	    writer.setDelegates(List.of(loggingEventWriter(), conditionalSlowWriter()));
	    return writer;
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

}
