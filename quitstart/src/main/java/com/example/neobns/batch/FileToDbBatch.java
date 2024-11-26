package com.example.neobns.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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

import com.example.neobns.dto.AccountDTO;

@Configuration
public class FileToDbBatch {
	
	private final DataSource datasource;
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	public FileToDbBatch(@Qualifier("targetDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}
	
	@Bean
	public Job fileToDBJob() {
		return new JobBuilder("fileToDBJob", jobRepository)
				.start(fileToDBStep()).build();
	}
	
	@Bean
	public Step fileToDBStep() {
		return new StepBuilder("fileToDBStep", jobRepository)
				.<AccountDTO, AccountDTO>chunk(100, transactionManager)
				.reader(fileReader())
				.writer(fileToDbWriter())
				.taskExecutor(fileToDBTaskExecutor())
				.build();
				
	}
	
	@Bean
	public TaskExecutor fileToDBTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("fileToDBTask");
		executor.initialize();
		return executor;
	}
	
	@Bean
	public FlatFileItemReader<AccountDTO> fileReader(){
		FlatFileItemReader<AccountDTO> reader = new FlatFileItemReader<>();
		String path = "C:/csv/test.csv";
		reader.setResource(new FileSystemResource(path));
		reader.setLinesToSkip(1);
		
		DefaultLineMapper<AccountDTO> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames("id", "accountNumber", "money", "name");
		lineMapper.setLineTokenizer(tokenizer);
		
		BeanWrapperFieldSetMapper<AccountDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(AccountDTO.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		reader.setLineMapper(lineMapper);
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<AccountDTO> fileToDbWriter(){
		return new JdbcBatchItemWriterBuilder<AccountDTO>()
				.dataSource(datasource)
				.sql("INSERT INTO account(accountNumber, money, name) VALUES (:accountNumber, :money, :name)")
				.beanMapped()
				.build();
	}

}
