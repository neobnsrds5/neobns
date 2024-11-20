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

import com.example.demo.TransferDTO;
import com.example.demo.entity.TransferEntity;

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
	public Step sendDataStep(ItemReader<TransferEntity> myReader,
			ItemProcessor<TransferEntity, TransferDTO> processor,
			ItemWriter<TransferDTO> writer) {
		return new StepBuilder("sendDataStep", jobRepository)
				.<TransferEntity, TransferDTO> chunk(10, transactionManager)
				.reader(myReader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean
	public JpaPagingItemReader<TransferEntity> myReader(EntityManagerFactory entityManagerFactory){
		return new JpaPagingItemReaderBuilder<TransferEntity>()
				.name("myReader")
				.entityManagerFactory(entityManagerFactory)
				.queryString("SELECT t from TransferEntity t")
				.pageSize(10)
				.build();
	}
	
	@Bean
	public ItemProcessor<TransferEntity, TransferDTO> processor(){
		return entity -> TransferDTO.builder()
				.fromAccount(entity.getFromAccount())
				.toAccount(entity.getToAccount())
				.money(entity.getMoney())
				.build();
		
	}
	
	@Bean
	public ItemWriter<TransferDTO> writer(RestTemplate restTemplate){
		return items -> {
			for (TransferDTO item : items) {
				restTemplate.postForObject("http://ap.example.com/data", item, Void.class);
			}
		};
	}

	
}
