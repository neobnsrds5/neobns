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
public class dbToApiBatch {
	
	private final DataSource datasource;
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	private final RestTemplate restTemplate;
	
	public dbToApiBatch(@Qualifier("dataDBSource") DataSource datasource,
			PlatformTransactionManager transactionManager,
			JobRepository jobRepository,
			RestTemplate restTemplate) {
		this.datasource = datasource;
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
		this.restTemplate = restTemplate;
	}
	
	@Bean
	public TaskExecutor toApiTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("dbToApiTask");
		executor.initialize();
		return executor;
	}
	
	@Bean
	public Job toApiJob() throws Exception{
		return new JobBuilder("dbToApiJob", jobRepository)
				.start(toApiStep())
				.build();
	}
	
	@Bean
	public Step toApiStep() throws Exception{
		return new StepBuilder("dbToApiStep", jobRepository)
				.<Map<String, Object>, AccountDTO>chunk(10, transactionManager)
				.reader(toApiReader())
				.processor(toApiProcessor())
				.writer(toApiWriter())
				.taskExecutor(toApiTaskExecutor())
				.build();
	}
	
	@Bean
	public JdbcPagingItemReader<Map<String, Object>> toApiReader() throws Exception{
		JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<Map<String, Object>>();
		reader.setDataSource(datasource);
		reader.setName("pagingReader");
		reader.setQueryProvider(toApiQueryProvider());
		reader.setRowMapper((rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", rs.getString("id"));
			map.put("accountNumber", rs.getString("accountNumber"));
			map.put("money", rs.getString("money"));
			map.put("name", rs.getString("name"));
			return map;
		});
		reader.setPageSize(10);
		return reader;
	}
	
	@Bean
	public PagingQueryProvider toApiQueryProvider() throws Exception{
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(datasource);
		factory.setSelectClause("SELECT id, accountNumber, money, name");
		factory.setFromClause("FROM account");
		factory.setSortKey("id");
		return factory.getObject();
	}
	
	@Bean
	public ItemProcessor<Map<String, Object>, AccountDTO> toApiProcessor(){
		return new ItemProcessor<Map<String, Object>, AccountDTO>() {
			
			@Override
			public AccountDTO process(Map<String, Object> item) throws Exception {
				AccountDTO result = new AccountDTO();
				result.setId((long)item.get("id"));
				result.setAccountNumber((String)item.get("accountNumber"));
				result.setMoney((long)item.get("money"));
				result.setName((String)item.get("name"));
				return result;
			}
		};
	}
	
	@Bean
	public ItemWriter<AccountDTO> toApiWriter(){
		return chunk -> chunk.getItems().parallelStream().forEach(this::sendToApi);
	}
	
	private void sendToApi(AccountDTO data) {
		String result = restTemplate.postForObject("http://localhost:8084/test", data, String.class);
		System.out.println(result);
	}
	
}
