package com.example.neobns.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
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
				.processor(logProcessor())
				.writer(logWriter())
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
		String path = "logs/application.log";
		reader.setResource(new FileSystemResource(path));

		DefaultLineMapper<LogDTO> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(";");
		tokenizer.setNames("timestmp", "logger_name", "level_string", "thread_name", "arg0", "arg1", "arg2", "arg3");
		lineMapper.setLineTokenizer(tokenizer);

		BeanWrapperFieldSetMapper<LogDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(LogDTO.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		reader.setLineMapper(lineMapper);
		return reader;
	}

	@Bean
	public ItemProcessor<LogDTO, LogDTO> logProcessor() {
		return new ItemProcessor<LogDTO, LogDTO>() {
			@Override
			public LogDTO process(LogDTO logDTO) throws Exception {
				// formatted_message 필드를 생성합니다.
				String formattedMessage = String
						.format("%s; %s; %s; %s;",
								logDTO.getArg0() != null ? logDTO.getArg0() : "",
								logDTO.getArg1() != null ? logDTO.getArg1() : "",
								logDTO.getArg2() != null ? logDTO.getArg2() : "",
								logDTO.getArg3() != null ? logDTO.getArg3() : "")
						.trim();
				logDTO.setFormatted_message(formattedMessage);

				return logDTO;
			}
		};

	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> logWriter() {
		String sql = "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, arg0, arg1, arg2, arg3) "
				+ "VALUES (UNIX_TIMESTAMP(:timestmp), :formatted_message, :logger_name, :level_string, :thread_name, :arg0, :arg1, :arg2, :arg3)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();
	}

}
