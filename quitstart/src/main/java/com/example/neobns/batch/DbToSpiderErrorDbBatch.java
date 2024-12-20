package com.example.neobns.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
	private String lastTimeStmp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	
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
		reader.setName("spiderReader");
		reader.setQueryProvider(spiderQueryProvider());
		reader.setRowMapper(new BeanPropertyRowMapper<>(LogDTO.class));
		reader.setPageSize(10);

		Map<String, Object> paramVal = new HashMap<>();
		paramVal.put("lastTimeStmp", lastTimeStmp);
		reader.setParameterValues(paramVal);

		return reader;
	}

	@Bean
	public PagingQueryProvider spiderQueryProvider() throws Exception {
		// 서버 실행 시간 이후의 에러 기록을 에러 히스토리 테이블에 전달
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(realSource);
		factory.setSelectClause("SELECT *");
		factory.setFromClause("FROM logging_error");
		factory.setWhereClause("WHERE STR_TO_DATE(timestmp, '%Y-%m-%d %H:%i:%s.%f') > STR_TO_DATE(:lastTimeStmp, '%Y-%m-%d %H:%i:%s.%f')");
		factory.setSortKey("event_id");

		return factory.getObject();
	}

	@Bean
	public ItemProcessor<LogDTO, FwkErrorHisDto> spiderProcessor() {
		return new ItemProcessor<LogDTO, FwkErrorHisDto>() {

			@Override
			public FwkErrorHisDto process(LogDTO item) throws Exception {
				lastTimeStmp = item.getTimestmp();
				return item.convertToHisDto();
			}
		};
	}

	@Bean
	public JdbcBatchItemWriter<FwkErrorHisDto> spiderWriter() {
		return new JdbcBatchItemWriterBuilder<FwkErrorHisDto>().dataSource(spiderDataSource).sql(
				"INSERT INTO SYSTEM.FWK_ERROR_HIS(ERROR_CODE, ERROR_SER_NO, CUST_USER_ID, ERROR_MESSAGE, ERROR_OCCUR_DTIME, ERROR_URL, ERROR_TRACE, ERROR_INSTANCE_ID) VALUES(:errorCode, :errorSerNo, :custUserId, :errorMessage, :errorOccurDtime, :errorUrl, :errorTrace, :errorInstanceId)")
				.beanMapped().build();
	}

	@Bean
	public Step spiderStep() throws Exception {

		int chunkSize = 100; // 10, 50, 100

		return new StepBuilder("spiderStep", jobRepository).<LogDTO, FwkErrorHisDto>chunk(chunkSize, transactionManager)
				.reader(spiderReader()).processor(spiderProcessor()).writer(spiderWriter())
				.taskExecutor(spiderExecutor()).build();
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
