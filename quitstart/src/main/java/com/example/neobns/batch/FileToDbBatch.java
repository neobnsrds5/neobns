package com.example.neobns.batch;

import java.util.Map;

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
import org.springframework.core.io.ClassPathResource;
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
	private final CustomBatchJobListener listener;
	
	public FileToDbBatch(@Qualifier("targetDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, CustomBatchJobListener listener) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.listener = listener;
	}
	
	@Bean
	public Job fileToDBJob() {
		return new JobBuilder("fileToDBJob", jobRepository)
				.start(fileToDBStep()).listener(listener).build();
	}
	
	@Bean
	public Step fileToDBStep() {
		
		int chunkSize = 10; // 10, 50, 100
		
		return new StepBuilder("fileToDBStep", jobRepository)
				.<AccountDTO, AccountDTO>chunk(chunkSize, transactionManager)
				.reader(fileReader())
				.processor(dummyProcessor2())
				.writer(fileToDbWriter())
				.taskExecutor(fileToDBTaskExecutor())
				.build();
				
	}
	
	
	@Bean
	public ItemProcessor<AccountDTO, AccountDTO> dummyProcessor2() {
		return new ItemProcessor<AccountDTO, AccountDTO>() {
			
			@Override
			public AccountDTO process(AccountDTO item) throws Exception {
				// dummy processor logic 추가
				for (int i = 0; i < 5; i++) {
					System.out.println("dummy processor is processing2 " + item.toString());
				}
				return item;
			}
		};
	}

	@Bean
	public TaskExecutor fileToDBTaskExecutor() {
		
		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; //50~100
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("fileToDBTask");
		executor.initialize();
		return executor;
	}
	
	@Bean
	public FlatFileItemReader<AccountDTO> fileReader(){
		FlatFileItemReader<AccountDTO> reader = new FlatFileItemReader<>();
		String path = "csv/test.csv";
//		reader.setResource(new FileSystemResource(path));
		reader.setResource(new ClassPathResource(path));
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
