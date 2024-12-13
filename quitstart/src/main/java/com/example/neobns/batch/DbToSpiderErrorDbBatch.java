package com.example.neobns.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.neobns.dto.FwkErrorHisDto;
import com.example.neobns.dto.LogDTO;

@Configuration
public class DbToSpiderErrorDbBatch {

	private final DataSource realSource;
	private final DataSource spiderDataSource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final CustomBatchJobListener listener;

	public DbToSpiderErrorDbBatch(@Qualifier("dataDataSource") DataSource realSource,
			@Qualifier("spiderDataSource") DataSource spiderDataSource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, CustomBatchJobListener listener) {
		this.realSource = realSource;
		this.spiderDataSource = spiderDataSource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.listener = listener;
	}

	@Bean
	public JdbcPagingItemReader<LogDTO> spiderReader() throws Exception {
		JdbcPagingItemReader<LogDTO> reader = new JdbcPagingItemReader<LogDTO>();
		reader.setDataSource(realSource);
		reader.setName("spiderPagingReader");
		reader.setQueryProvider(queryProvider());
		reader.setRowMapper(new BeanPropertyRowMapper<>(LogDTO.class));
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public PagingQueryProvider queryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(realSource);
		factory.setSelectClause("SELECT *");
		factory.setFromClause("FROM LOGGING_ERROR");
		factory.setSortKey("EVENT_ID");
		return factory.getObject();
	}
	
	@Bean
	public ItemProcessor<LogDTO, FwkErrorHisDto> spiderProcessor(){
		return logDTO -> logDTO.convertToHisDto();
	}

	@Bean
	public JdbcBatchItemWriter<FwkErrorHisDto> writer() {
		return new JdbcBatchItemWriterBuilder<FwkErrorHisDto>()
				.dataSource(spiderDataSource)
				.sql(
				"INSERT INTO SYSTEM.FWK_ERROR_HIS(ERROR_CODE, ERROR_SER_NO, CUST_USER_ID, ERROR_MESSAGE, ERROR_OCCUR_DTIME, ERROR_URL, ERROR_TRACE, ERROR_INSTANCE_ID) VALUES(:ERROR_CODE, :ERROR_SER_NO, :CUST_USER_ID, :ERROR_MESSAGE, :ERROR_OCCUR_DTIME, :ERROR_URL, :ERROR_TRACE, :ERROR_INSTANCE_ID);")
				.beanMapped().build();
	}

	@Bean
	public Step spiderStep() throws Exception {

		int chunkSize = 100; // 10, 50, 100

		return new StepBuilder("dbCopyStep", jobRepository)
				.<LogDTO, FwkErrorHisDto>chunk(chunkSize, transactionManager)
				.reader(spiderReader())
				.processor(spiderProcessor()).writer(writer()).taskExecutor(spiderExecutor()).build();
	}

	@Bean
	public Job spiderJob() throws Exception {
		return new JobBuilder("dbToSpiderErrorJob", jobRepository).start(spiderStep()).listener(listener).build();
	}

	@Bean
	public TaskExecutor spiderExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("dbToSpiderErrorJob");
		executor.initialize();
		return executor;
	}

}
