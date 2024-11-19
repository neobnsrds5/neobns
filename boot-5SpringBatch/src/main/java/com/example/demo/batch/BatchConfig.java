package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.example.demo.MyDTO;
import com.example.demo.entity.MyEntity;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Bean
	public Job sendDataJob(Step sendDataStep) {
		return new JobBuilder("sendDataJob", jobRepository)
				.start(sendDataStep)
				.build();
	}
	
	@Bean
	public Step sendDataStep(ItemReader<MyEntity> myReader,
			ItemProcessor<MyEntity, MyDTO> processor,
			ItemWriter<MyDTO> writer) {
		return new StepBuilder("sendDataStep", jobRepository)
				.<MyEntity, MyDTO> chunk(10, transactionManager)
				.reader(myReader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean
	public JpaPagingItemReader<MyEntity> myReader(EntityManagerFactory entityManagerFactory){
		return new JpaPagingItemReaderBuilder<MyEntity>()
				.name("myReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("SELECT e from MyEntity e")
				.pageSize(10)
				.build();
	}
	
	@Bean
	public ItemProcessor<MyEntity, MyDTO> processor(){
		return entity -> MyDTO.builder()
				.fromAccount(entity.getFromAccount())
				.toAccount(entity.getToAccount())
				.money(entity.getMoney())
				.build();
		
	}
	
	@Bean
	public ItemWriter<MyDTO> writer(RestTemplate restTemplate){
		return items -> {
			for (MyDTO item : items) {
				restTemplate.postForObject("http://ap.example.com/data", item, Void.class);
			}
		};
	}

	
}
