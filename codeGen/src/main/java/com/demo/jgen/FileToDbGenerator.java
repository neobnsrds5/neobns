package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;

/*
 * File To DB 배치 코드 생성기
 */
public class FileToDbGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
    	// Extracting schema properties (fields)
    	// yml 파일에서 components > schemas > properties에 정의된 속성의 이름과 타입 정보를 Map 형태로 반환
        Map<String, Schema> properties = schema.getProperties();
    	StringBuilder tokenizerBuilder = new StringBuilder();
        StringBuilder sqlBuilder = new StringBuilder();

        // CSV 토큰 생성
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            tokenizerBuilder.append("\"").append(fieldName).append("\", ");
        }
        // Remove trailing commas and spaces (마지막 , 제거)
        if (tokenizerBuilder.length() > 0) tokenizerBuilder.setLength(tokenizerBuilder.length() - 2);

        // fileToDbWriter 에서 사용될 INSERT문 생성
        sqlBuilder.append("INSERT INTO ").append(resourceName.toLowerCase())
                .append(" (").append(String.join(", ", properties.keySet()))
                .append(") VALUES (:").append(String.join(", :", properties.keySet())).append(")");

        String batchCode = """
		        package %s;
		
		        import javax.sql.DataSource;
		        import org.springframework.batch.core.Job;
		        import org.springframework.batch.core.Step;
		        import org.springframework.batch.core.job.builder.JobBuilder;
		        import org.springframework.batch.core.repository.JobRepository;
		        import org.springframework.batch.core.step.builder.StepBuilder;
		        import org.springframework.batch.item.file.FlatFileItemReader;
		        import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
		        import org.springframework.batch.item.file.mapping.DefaultLineMapper;
		        import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
		        import org.springframework.batch.item.database.JdbcBatchItemWriter;
		        import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
		        import org.springframework.beans.factory.annotation.Qualifier;
		        import org.springframework.context.annotation.Bean;
		        import org.springframework.context.annotation.Configuration;
		        import org.springframework.core.io.FileSystemResource;
		        import org.springframework.transaction.PlatformTransactionManager;
		        import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
		        import org.springframework.core.task.TaskExecutor;
		
		        @Configuration
		        public class %sFileToDbBatch {
		
		            private final DataSource dataSource;
		            private final JobRepository jobRepository;
		            private final PlatformTransactionManager transactionManager;
		
		            public %sFileToDbBatch(@Qualifier(\"targetDBSource\") DataSource dataSource,
		                    JobRepository jobRepository,
		                    PlatformTransactionManager transactionManager) {
		                this.dataSource = dataSource;
		                this.jobRepository = jobRepository;
		                this.transactionManager = transactionManager;
		            }
		
		            @Bean
		            public Job fileToDbJob() {
		                return new JobBuilder(\"fileToDbJob\", jobRepository)
		                        .start(fileToDbStep())
		                        .build();
		            }
		
		            @Bean
		            public Step fileToDbStep() {
		                return new StepBuilder(\"fileToDbStep\", jobRepository)
		                        .<%sDto, %sDto>chunk(100, transactionManager)
		                        .reader(fileReader())
		                        .writer(fileToDbWriter())
		                        .taskExecutor(taskExecutor())
		                        .build();
		            }
		
		            @Bean
		            public FlatFileItemReader<%sDto> fileReader() {
		                FlatFileItemReader<%sDto> reader = new FlatFileItemReader<>();
		                reader.setResource(new FileSystemResource(\"파일 경로\"));
		                reader.setLinesToSkip(1);
		                DefaultLineMapper<%sDto> lineMapper = new DefaultLineMapper<>();
		                DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		                tokenizer.setNames(%s);
		                lineMapper.setLineTokenizer(tokenizer);
		                BeanWrapperFieldSetMapper<%sDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		                fieldSetMapper.setTargetType(%sDto.class);
		                lineMapper.setFieldSetMapper(fieldSetMapper);
		                reader.setLineMapper(lineMapper);
		                return reader;
		            }
		
		            @Bean
		            public JdbcBatchItemWriter<%sDto> fileToDbWriter() {
		                return new JdbcBatchItemWriterBuilder<>()
		                        .dataSource(dataSource)
		                        .sql("%s")
		                        .beanMapped()
		                        .build();
		            }
		
		            @Bean
		            public TaskExecutor taskExecutor() {
		                ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		                executor.setCorePoolSize(4);
		                executor.setMaxPoolSize(8);
		                executor.setQueueCapacity(50);
		                executor.setThreadNamePrefix(\"fileToDbTask\");
		                executor.initialize();
		                return executor;
		            }
		        }
		        """.formatted(
		            packageName, resourceName, resourceName, // 패키지명, 클래스명, 생성자명
		            resourceName, resourceName, // fileToDbStep()
		            resourceName, resourceName, resourceName, tokenizerBuilder.toString(), resourceName, resourceName, // fileReader()
		            resourceName, sqlBuilder.toString()); // fileToDbWriter()

        // Write the generated code to a file
        writeToFile(packageDir + resourceName + "FileToDbBatch.java", batchCode);
    }
}
