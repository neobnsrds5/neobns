package neo.spider.solution.batch.batchJobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SubBatch2 {

	private JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private static final Logger logger = LoggerFactory.getLogger(SubBatch2.class);
	// 오라클 스파이더 배치 테이블로 통합 가능하게 하는 리스너로 현재 오라클 스파이더 배치 테이블 사용할 수 없어 주석처리
//		private final CustomBatchJobListener listener;

	public SubBatch2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
	}

	@Bean
	public Job subBatch2Job() {
		return new JobBuilder("subBatch2", jobRepository)/* .listener(listener) */.start(subBatch2Step()).build();

	}

	@Bean
	public Step subBatch2Step() {

		return (Step) new StepBuilder("subBatch2Step", jobRepository).tasklet((stepContribution, chunkContext) -> {
			logger.info("Sub Batch2 실행 중");
			Thread.sleep(1000);
			logger.info("Sub Batch2 실행 완료");
			return null;
		}, platformTransactionManager).build();

	}
}
