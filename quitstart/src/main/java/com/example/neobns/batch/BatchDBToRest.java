package com.example.neobns.batch;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.entity.TransferEntity;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchDBToRest {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final RestTemplate restTemplate;
    
    @Qualifier("dataDBSource")
    private final DataSource dataSource;

    @Bean
    public Job toRestJob() throws Exception{
        return new JobBuilder("toRestJob", jobRepository)
                .start(partitionedStep())
                .build();
    }

    @Bean
    public Step partitionedStep() throws Exception{
        return new StepBuilder("partitionedStep", jobRepository)
                .partitioner("toRestStep", partitioner())
                .partitionHandler(partitionHandler())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        return gridSize -> {
        	Map<String, ExecutionContext> partitions = new HashMap<>();
        	int range = 1000;
        	int min = 1;
        	int max = 100;
        	
        	int numberOfPartions = (max- min + range + 1) /range;
        	for (int i = 0; i < numberOfPartions; i++) {
				ExecutionContext context = new ExecutionContext();
				int start = min + (i*range);
				int end = Math.min(start + range -1 , max);
				
				context.putString("minValue", String.valueOf(start));
				context.putString("maxValue", String.valueOf(end));
				
				partitions.put("partition"+i, context);
			}
        	return partitions;
        };
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler() throws Exception{
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setStep(toRestStep());
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        handler.setGridSize(4); // 병렬 처리의 스레드 개수
        return handler;
    }

    @Bean
    public Step toRestStep() throws Exception{
        return new StepBuilder("toRestStep", jobRepository)
                .<TransferEntity, TransferDTO>chunk(10, platformTransactionManager)
                .reader(transferBatchReader(null, null))
                .processor(transferBatchProcessor())
                .writer(transferBatchWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<TransferEntity> transferBatchReader(
    		@Value("#{stepExecutionContext['minValue']}") String minValue,
    		@Value("#{stepExecutionContext['minValue']}") String maxValue) throws Exception{
        return new JdbcPagingItemReaderBuilder<TransferEntity>()
                .name("transferBatchReader")
                .dataSource(dataSource)
                .fetchSize(10)
                .beanRowMapper(TransferEntity.class)
                .queryProvider(pagingQueryProvider().getObject())
                .parameterValues(new HashMap<>(Map.of("minValue", minValue, "maxValue", maxValue)))
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider(){
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT *");
        provider.setFromClause("FROM transfer_entity");
        provider.setWhereClause("WHERE id BETWEEN :minValue AND :maxValue");
        provider.setSortKey("id");
        return provider;
    }

    @Bean
    public ItemProcessor<TransferEntity, TransferDTO> transferBatchProcessor() {
        return item -> TransferDTO.builder()
                .fromAccount(item.getFromAccount())
                .toAccount(item.getToAccount())
                .money(item.getMoney())
                .build();
    }

    @Bean
    public ItemWriter<TransferDTO> transferBatchWriter() {
        return items -> {
            for (TransferDTO item : items) {
                Boolean success = restTemplate.postForObject("http://localhost:8082/transfer", item, Boolean.class);
                System.out.println("transaction : " + success);
            }
        };
    }
}
