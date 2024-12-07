package com.example.neobns.batch;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DbToDbBatch {
	
	private final DataSource realSource;
	private final DataSource targetSource;
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	public DbToDbBatch(@Qualifier("dataDataSource") DataSource realSource,
			@Qualifier("targetDataSource") DataSource targetSource,
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		this.realSource = realSource;
		this.targetSource = targetSource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}
	
	@Bean
	public JdbcPagingItemReader<Map<String, Object>> reader() throws Exception{
		JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<Map<String,Object>>();
		reader.setDataSource(realSource);
		reader.setName("pagingReader");
		reader.setQueryProvider(queryProvider());
		reader.setRowMapper((rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("accountNumber", rs.getString("accountNumber"));
			map.put("money", rs.getLong("money"));
			map.put("name", rs.getString("name"));
			return map;
		});
		reader.setPageSize(10);
		return reader;
	}
	
	@Bean
	public PagingQueryProvider queryProvider() throws Exception{
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(realSource);
		factory.setSelectClause("SELECT *");
		factory.setFromClause("FROM account");
		factory.setSortKey("id");
		return factory.getObject();
	}
	
	@Bean
	public JdbcBatchItemWriter<Map<String, Object>> writer(){
		return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
				.dataSource(targetSource)
				.sql("INSERT INTO account(accountNumber, money, name) VALUES (:accountNumber, :money, :name)")
				.itemSqlParameterSourceProvider(item -> {
					MapSqlParameterSource params = new MapSqlParameterSource();
					params.addValue("accountNumber", item.get("accountNumber"));
					params.addValue("money", item.get("money"));
					params.addValue("name", item.get("name"));
					return params;
				}).build();
	}
	
	@Bean
	public Step step() throws Exception{
		return new StepBuilder("dbCopyStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(100, transactionManager)
				.reader(reader())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build();
	}
	
	@Bean
	public Job job() throws Exception{
		return new JobBuilder("dbCopyJob", jobRepository)
//				.start(partitoinStep())
				.start(step())
				.build();
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("dbCopyTask");
		executor.initialize();
		return executor;
	}
	
}
