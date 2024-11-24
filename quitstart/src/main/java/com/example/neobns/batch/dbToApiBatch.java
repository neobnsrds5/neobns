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

import com.example.neobns.dto.ItemDto;

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
				.<Map<String, String>, ItemDto>chunk(10, transactionManager)
				.reader(toApiReader())
				.processor(toApiProcessor())
				.writer(toApiWriter())
				.taskExecutor(toApiTaskExecutor())
				.build();
	}
	
	@Bean
	public JdbcPagingItemReader<Map<String, String>> toApiReader() throws Exception{
		JdbcPagingItemReader<Map<String, String>> reader = new JdbcPagingItemReader<Map<String, String>>();
		reader.setDataSource(datasource);
		reader.setName("pagingReader");
		reader.setQueryProvider(toApiQueryProvider());
		reader.setRowMapper((rs, rowNum) -> {
			Map<String, String> map = new HashMap<>();
			map.put("id", rs.getString("id"));
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
		factory.setSelectClause("SELECT id, name");
		factory.setFromClause("FROM `before`");
		factory.setSortKey("id");
		return factory.getObject();
	}
	
	@Bean
	public ItemProcessor<Map<String, String>, ItemDto> toApiProcessor(){
		return new ItemProcessor<Map<String, String>, ItemDto>() {
			
			@Override
			public ItemDto process(Map<String, String> item) throws Exception {
				ItemDto result = new ItemDto();
				result.setId(item.get("id"));
				result.setName(item.get("name"));
				return result;
			}
		};
	}
	
	@Bean
	public ItemWriter<ItemDto> toApiWriter(){
		return chunk -> chunk.getItems().parallelStream().forEach(this::sendToApi);
	}
	
	private void sendToApi(ItemDto data) {
		String result = restTemplate.postForObject("http://localhost:8084/test", data, String.class);
		System.out.println(result);
	}
	
}
