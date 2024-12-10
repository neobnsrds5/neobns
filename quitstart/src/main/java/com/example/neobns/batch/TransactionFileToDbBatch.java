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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;

import com.example.neobns.dto.AccountDTO;
import com.example.neobns.service.BatchService;

@Configuration
public class TransactionFileToDbBatch {

	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final BatchService batchService;

	public TransactionFileToDbBatch(@Qualifier("targetDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, BatchService batchService) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.batchService = batchService;
	}

	@Bean
	public Job TransactionJob() {
		return new JobBuilder("TransactionJob", jobRepository).start(TransactionStep()).build();
	}

	@Bean
	public Step TransactionStep() {

		int chunkSize = 10;

		return new StepBuilder("TransactionStep", jobRepository)
				.<AccountDTO, AccountDTO>chunk(chunkSize, transactionManager)
				.reader(TransactionReader())
				.processor(TransactionProcessor())
				.writer(TransactionWriter())
				.taskExecutor(TransactionExecutor())
				.build();

	}

	@Bean
	public ItemProcessor<AccountDTO, AccountDTO> TransactionProcessor() {
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
	public TaskExecutor TransactionExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("TransactionTask");
		executor.initialize();
		return executor;
	}

	@Bean
	public FlatFileItemReader<AccountDTO> TransactionReader() {
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
	@SpiderTransaction(propagation = Propagation.REQUIRED, timeout = 1)
	public JdbcBatchItemWriter<AccountDTO> TransactionWriter() {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new JdbcBatchItemWriterBuilder<AccountDTO>().dataSource(datasource)
				.sql("INSERT INTO account(accountNumber, money, name) VALUES (:accountNumber, :money, :name)")
				.beanMapped().build();
	}

}
