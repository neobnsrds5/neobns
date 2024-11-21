package com.example.neobns.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.entity.TransferEntity;
import com.example.neobns.repository.TransferRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchDBToRest {
	
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final RestTemplate restTemplate;
	
	private final TransferRepository repository;
	
	@Bean
	public Job toRestJob() {
		return new JobBuilder("toRestJob", jobRepository)
				.start(toRestStep())
				.build();
	}
	
	@Bean
	public Step toRestStep() {
		return new StepBuilder("toRestStep", jobRepository)
				.<TransferEntity, TransferDTO> chunk(0, platformTransactionManager)
				.reader(transferBatchReader())
				.processor(transferBatchProcessor())
				.writer(transferBatchWriter())
				.build();
	}
	
	@Bean
	public RepositoryItemReader<TransferEntity> transferBatchReader(){
		return new RepositoryItemReaderBuilder<TransferEntity>()
				.name("transferBatchReader")
				.pageSize(10)
				.methodName("findAll")
				.repository(repository)
				.sorts(Map.of("id", Sort.Direction.ASC))
				.build();
	}
	
	@Bean
	public ItemProcessor<TransferEntity, TransferDTO> transferBatchProcessor(){
		return new ItemProcessor<TransferEntity, TransferDTO>() {
			
			@Override
			public TransferDTO process(TransferEntity item) throws Exception {
				// TODO Auto-generated method stub
				return TransferDTO.builder()
						.fromAccount(item.getFromAccount())
						.toAccount(item.getToAccount())
						.money(item.getMoney())
						.build();
				
			}
		};
	}
	
	@Bean
	public ItemWriter<TransferDTO> transferBatchWriter(){
		return items -> {
			for (TransferDTO item : items) {
				Boolean success = restTemplate.postForObject("http://localhost:8082/transfer", item, Boolean.class);
				System.out.println("transaction : " + success);
			}
		};
	}

}
