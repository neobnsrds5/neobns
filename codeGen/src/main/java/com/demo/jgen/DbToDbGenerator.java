package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;

/*
 * DB To DB 배치 코드 생성기
 */
public class DbToDbGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
    	// Extracting schema properties (fields)
    	// yml 파일에서 components > schemas > properties에 정의된 속성의 이름과 타입 정보를 Map 형태로 반환
        Map<String, Schema> properties = schema.getProperties();
        StringBuilder mapBuilder = new StringBuilder();
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = new StringBuilder();
        String tableName = resourceName.toLowerCase();

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.toLowerCase().equals("id"))
                continue;
            String getMethod = OpenApiCodeGenerator.mapSchemaTypeToJavaType(entry.getValue().getType(), entry.getValue().getFormat());
            
            // JdbcPagingItemReader<Map<String, Object>> reader()에 사용될 Map put
            mapBuilder.append("map.put(\"")
                    .append(fieldName).append("\", rs.get")
                    .append(getMethod).append("(\"")
                    .append(fieldName).append("\"));\n			");
            
            // JdbcBatchItemWriter<Map<String, Object>> writer()에 MapSqlParameterSource
            paramBuilder.append("params.addValue(\"")
        	.append(fieldName).append("\", item.get(\"")
        	.append(fieldName).append("\"));\n				");
        }
        
        // JdbcBatchItemWriter<Map<String, Object>> writer()에 사용될 INSERT문 생성
        sqlBuilder.append("INSERT INTO ").append(tableName).append(" (")
        		.append(String.join(", ", properties.keySet().stream()
        				.filter(field -> !field.equalsIgnoreCase("id"))
        				.toList()))
        		.append(") VALUES (:")
        		.append(String.join(", :", properties.keySet().stream()
        				.filter(field -> !field.equalsIgnoreCase("id"))
        				.toList()))
        		.append(")");

        String batchCode = """
				package %s;
				
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
				import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
				import org.springframework.transaction.PlatformTransactionManager;
				
				@Configuration
				public class %sDbToDbBatch {
				
					private final DataSource realSource;
					private final DataSource targetSource;
				
					private final JobRepository jobRepository;
					private final PlatformTransactionManager transactionManager;
				
					public %sDbToDbBatch(@Qualifier("dataDBSource") DataSource realSource,
							@Qualifier("targetDBSource") DataSource targetSource,
							JobRepository jobRepository,
							PlatformTransactionManager transactionManager) {
						this.realSource = realSource;
						this.targetSource = targetSource;
						this.jobRepository = jobRepository;
						this.transactionManager = transactionManager;
					}
				
					@Bean
					public JdbcPagingItemReader<Map<String, Object>> reader() throws Exception {
						JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<>();
						reader.setDataSource(realSource);
						reader.setName("pagingReader");
						reader.setQueryProvider(queryProvider());
						reader.setRowMapper((rs, rowNum) -> {
							Map<String, Object> map = new HashMap<>();
							%s
							return map;
						});
						reader.setPageSize(10);
						return reader;
					}
				
					@Bean
					public PagingQueryProvider queryProvider() throws Exception {
						SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
						factory.setDataSource(realSource);
						factory.setSelectClause("SELECT *");
						factory.setFromClause("FROM %s");
						factory.setSortKey("id");
						return factory.getObject();
					}
				
					@Bean
					public JdbcBatchItemWriter<Map<String, Object>> writer() {
						return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
							.dataSource(targetSource)
							.sql("%s")
							.itemSqlParameterSourceProvider(item -> {
								MapSqlParameterSource params = new MapSqlParameterSource();
								%s
								return params;
							})
							.build();
					}
				
					@Bean
					public Step step() throws Exception {
						return new StepBuilder("dbCopyStep", jobRepository)
							.<Map<String, Object>, Map<String, Object>>chunk(100, transactionManager)
							.reader(reader())
							.writer(writer())
							.taskExecutor(taskExecutor())
							.build();
					}
				
					@Bean
					public Job job() throws Exception {
						return new JobBuilder("dbCopyJob", jobRepository)
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
        		""".formatted(
        			packageName, resourceName, resourceName, // 패키지명, 클래스명, 생성자명
        			mapBuilder.toString(), // reader()의 setRowMapper
        			tableName, // queryProvider()의 SELECT문
        			sqlBuilder.toString(), // writer()의 INSERT문
        			paramBuilder.toString()); // writer()의 MapSqlParameterSource
        
        // Write the generated code to a file
        writeToFile(packageDir + resourceName + "DbToDbBatch.java", batchCode);
	}
}
