package com.example.neobns.batch;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.dto.AccountDTO;

@Configuration
public class DbToApiBatch {

	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final RestTemplate restTemplate;
	private final CustomBatchJobListener listener;
	
	public DbToApiBatch(@Qualifier("dataDataSource") DataSource datasource, PlatformTransactionManager transactionManager,
			JobRepository jobRepository, RestTemplate restTemplate, CustomBatchJobListener listener) {
		this.datasource = datasource;
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
		this.restTemplate = restTemplate;
		this.listener = listener;
	}
	
	@Bean
	public TaskExecutor toApiTaskExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; //50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("dbToApiTask");
		executor.initialize();
		return executor;
	}

	@Bean
	public Job toApiJob() throws Exception {
		return new JobBuilder("dbToApiJob", jobRepository).start(toApiStep()).listener(listener).build();
	}

	@Bean
	public Step toApiStep() throws Exception {
		int chunkSize = 500; // 10, 50, 100
		return new StepBuilder("dbToApiStep", jobRepository)
				.<Map<String, Object>, AccountDTO>chunk(chunkSize, transactionManager).reader(toApiReader())
				.processor(toApiProcessor()).writer(toApiWriter()).taskExecutor(toApiTaskExecutor()).build();
	}

	@Bean
	public JdbcPagingItemReader<Map<String, Object>> toApiReader() throws Exception {
		JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<Map<String, Object>>();
		reader.setDataSource(datasource);
		reader.setName("pagingReader");
		reader.setQueryProvider(toApiQueryProvider());
		reader.setRowMapper((rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", rs.getLong("id"));
			map.put("accountNumber", rs.getString("accountNumber"));
			map.put("money", rs.getLong("money"));
			map.put("name", rs.getString("name"));
			return map;
		});
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public PagingQueryProvider toApiQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(datasource);
		factory.setSelectClause("SELECT *");
		factory.setFromClause("FROM Account");
		factory.setSortKey("id");
		return factory.getObject();
	}

	@Bean
	public ItemProcessor<Map<String, Object>, AccountDTO> toApiProcessor() {
		return new ItemProcessor<Map<String, Object>, AccountDTO>() {

			@Override
			public AccountDTO process(Map<String, Object> item) throws Exception {
				
				String threadName = Thread.currentThread().getName();
				System.out.println("\tProcessing item: " + item.toString() + " on Thread: " + threadName);
				System.out.println("dummy processor is processing " + item.toString());
				// dummy processor logic 추가
//				for (int i = 0; i < item.size(); i++) {
//					System.out.println("dummy processor is processing " + item.toString());
//				}

				AccountDTO result = new AccountDTO();
				result.setId((long) item.get("id"));
				result.setAccountNumber((String) item.get("accountNumber"));
				result.setMoney((long) item.get("money"));
				result.setName((String) item.get("name"));
				return result;
			}
		};
	}

	@Bean
	public ItemWriter<AccountDTO> toApiWriter() {
		return chunk -> chunk.getItems().parallelStream().forEach(this::sendToApi);
	}

	private void sendToApi(AccountDTO data) {
		String result = restTemplate.postForObject("http://localhost:8084/test", data, String.class);
		System.out.println("API 응답 : " + result);
	}

}