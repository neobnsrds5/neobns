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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.function.context.config.ContextFunctionCatalogInitializer.DummyProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.neobns.dto.FwkErrorHisDto;

@Configuration
public class ScheduledErrorBatch {
	// 스케쥴을 통한 에러 테이블 데이터 이동
	/*
	 * private final DataSource spiderDataSource; private final JobRepository
	 * jobRepository; private final PlatformTransactionManager transactionManager;
	 * private final CustomBatchJobListener listener;
	 */
	/*
	 * public ScheduledErrorBatch(@Qualifier("spiderDataSource") DataSource
	 * spiderDataSource, JobRepository jobRepository, PlatformTransactionManager
	 * transactionManager, CustomBatchJobListener listener) { super();
	 * this.spiderDataSource = spiderDataSource; this.jobRepository = jobRepository;
	 * this.transactionManager = transactionManager; this.listener = listener; }
	 * 
	 * @Bean public JdbcPagingItemReader<FwkErrorHisDto> errorReader() throws
	 * Exception {
	 * 
	 * String lastProcessedTime = getLastProcessedTime();
	 * 
	 * Map<String, Object> paramVal = new HashMap<String, Object>();
	 * paramVal.put("lastProcessedTime", lastProcessedTime);
	 * 
	 * JdbcPagingItemReader reader = new
	 * JdbcPagingItemReaderBuilder<FwkErrorHisDto>().dataSource(spiderDataSource)
	 * .name("errorReader").determineQueryProvider(createPagingQueryProvider())
	 * .parameterValues(lastProcessedTime).
	 * 
	 * reader .setPageSize(10); return reader;
	 * 
	 * return null; }
	 * 
	 * @Bean public String getLastProcessedTime() { // TODO Auto-generated method
	 * stub return null; }
	 * 
	 * @Bean public PagingQueryProvider createPagingQueryProvider() throws Exception
	 * { SqlPagingQueryProviderFactoryBean factoryBean = new
	 * SqlPagingQueryProviderFactoryBean();
	 * factoryBean.setDataSource(spiderDataSource);
	 * factoryBean.setSelectClause("SELECT *");
	 * factoryBean.setFromClause("FROM FWK_ERROR_HIS");
	 * factoryBean.setWhereClause("WHERE ERROR_OCCUR_DTIME > :lastProcessedTime");
	 * factoryBean.setSortKey("ERROR_OCCUR_DTIME");
	 * 
	 * return factoryBean.getObject();
	 * 
	 * }
	 */

}
