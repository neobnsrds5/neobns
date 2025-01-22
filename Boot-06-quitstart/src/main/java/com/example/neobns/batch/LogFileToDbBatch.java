package com.example.neobns.batch;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.PartitionStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.neobns.dto.LogDTO;
import com.example.neobns.service.FileMaintenanceService;

@Configuration
public class LogFileToDbBatch {
	// 커스텀 디비 어펜더처럼 ps에 대입하는 방식으로 되어있는 지 확인할 것
	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
//	private final CustomBatchJobListener listener;
	private final FileMaintenanceService fileService;
	private String path = "../logs/application.log";
	private int gridSize = 2; // 현재는 파티셔닝 처리 안함
	private int chunkSize = 100;

	public LogFileToDbBatch(@Qualifier("dataDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			FileMaintenanceService fileMaintenanceService /* , CustomBatchJobListener listener */) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
//		this.listener = listener;
		this.fileService = fileMaintenanceService;
	}

	// 데이터의 양에 따라 실제로 가능한 그리드 사이즈 계산
	public long[] getRealGridSize() {
		long totalLines = fileService.findFileLinesCount(path);
		int partitionSize = (int) Math.ceil((double) totalLines / gridSize);
		int realGridSize = (int) Math.ceil((double) totalLines / partitionSize);
		long[] realSize = { totalLines, partitionSize, realGridSize };
		return realSize;
	}

	// Partition Size (Grid Size): 데이터를 논리적 파티션(Partition)으로 나누는 단위.
	// Chunk Size: 파티션 내부에서 데이터를 처리하는 단위.
	// 각 파티션의 StepExecution에서 Chunk 단위로 데이터를 처리.
	// ex) 30000 data , Partition Size 300, chunk size 50
	// -> 300개의 각 파티션은 30000/300=100개 데이터를 병렬 처리
	// -> 하나의 파티션에서 처리 chunk size 50개씩 병렬 처리

	@Bean
	public Job logToDBJob() {
		// 하나의 파일을 읽는 방식이기에 리더를 병렬처리 하는 파티셔닝 처리 보류
		return new JobBuilder("logToDBJob", jobRepository).start(logToDBStep())
				/* .start(masterStep()) *//* .listener(listener) */.build();
	}

	// 파티션 스텝
	@Bean
	public Step masterStep() {
		return new PartitionStepBuilder(new StepBuilder("masterStep", jobRepository))
				.partitioner("slaveStep", partitioner()) // 파티셔닝 설정
				.step(logToDBStep()) // 마스터의 슬레이브 스텝 설정
				.partitionHandler(partitionerHandler()) // 파티셔닝 핸들러 설정
				.build();

	}

	// 파티셔닝 설정. startline, endline을 컨텍스트에 넣어 reader에서 사용할 수 있게 함
	@Bean
	public Partitioner partitioner() {
		return gridSize -> {

			long[] realSize = getRealGridSize();

			long totalLines = realSize[0];
			int partitionSize = (int) realSize[1];
			int realGridSize = (int) realSize[2];

			Map<String, ExecutionContext> partitionMap = new HashMap<>();
			System.out.println("total line : " + totalLines);
			System.out.println("partition size : " + partitionSize);
			System.out.println("real grid size : " + realGridSize);

			for (int i = 0; i < realGridSize; i++) {
				ExecutionContext executionContext = new ExecutionContext();

				long start = 1 + i * partitionSize;
				long end = (start > totalLines) ? 0 : Math.min((i + 1) * partitionSize, totalLines);

				System.out.println("start : " + start + " end : " + end + " total line : " + totalLines);

				if (start > totalLines) {
					continue;
				}

				executionContext.putLong("start", start);
				executionContext.putLong("end", end);
				partitionMap.put("partition" + (i + 1), executionContext);

			}

			return partitionMap;

		};
	}

	// 파티셔닝 핸들러 설정
	@Bean
	public PartitionHandler partitionerHandler() {

		long[] realSize = getRealGridSize();
		int realGridSize = (int) realSize[2];

		TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
		handler.setGridSize(realGridSize); // 실제 그리드 사이즈
		handler.setStep(logToDBStep());
		handler.setTaskExecutor(logToDBTaskExecutor());

		return handler;
	}

	// 마스터의 슬레이브 스텝 설정
	@Bean
	public Step logToDBStep() {

		return new StepBuilder("logToDBStep", jobRepository).<LogDTO, LogDTO>chunk(chunkSize, transactionManager)
				.reader(logReader())
				/* 멀티 리소스 리더로 지정 */
				/* .reader(multiResourceItemReader()) */
				// 배치 성능 개선을 위해 dummy processor 주석처리
				/* .processor(dummyProcessor3()) */
				 .writer(compositeWriter())  /* .writer(dummyWriter()) */.taskExecutor(logToDBTaskExecutor()).build();
	}

	@Bean
	public ItemProcessor<LogDTO, LogDTO> dummyProcessor3() {
		return new ItemProcessor<LogDTO, LogDTO>() {

			@Override
			public LogDTO process(LogDTO item) throws Exception {
				String threadName = Thread.currentThread().getName();
				return item;
			}
		};
	}

	@Bean
	public TaskExecutor logToDBTaskExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("logToDBTask");
		executor.initialize();

		return executor;
	}

	// 현재 실행중인 step의 execution context를 가져오는 메서드
	public ExecutionContext getExecutionContext() {
		return StepSynchronizationManager.getContext().getStepExecution().getExecutionContext();
	}

	// 멀티 리소스 리더를 통해 롤링된 파일을 멀티 리소스로 지정
	@Bean
	public MultiResourceItemReader<LogDTO> multiResourceItemReader() {

		MultiResourceItemReader<LogDTO> multiReader = new MultiResourceItemReader<>();
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

		// 롤링된 분단위 리소스 지정
		Resource[] resource;
		try {
			// 프로젝트 폴더 경로 구한 후 \를 /로 바꿈
			String rootPath = System.getProperty("user.dir");
			int lastIndex = rootPath.lastIndexOf("\\");
			String rolledPath = "file:" + rootPath.substring(0, lastIndex)
					+ "\\logs\\application\\rolling\\application-*.log";
			rolledPath = rolledPath.replace("\\", "/");
			resource = patternResolver.getResources(rolledPath);
//			System.out.println("resource len : " + resource.length);
			multiReader.setResources(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 개별 파일을 담당할 리더 지정
		multiReader.setDelegate(logReader());

		return multiReader;

	}

	// 파티셔닝 컨텍스트에서 startline, endline을 읽어와 해당하는 것만 읽기 진행.
	@Bean
	/* @StepScope */
	public FlatFileItemReader<LogDTO> logReader() {

		// 파티셔닝 적용을 위해 저장된 값 가져옴
//		long start = getExecutionContext().getLong("start");
//		long end = getExecutionContext().getLong("end");
//
//		System.out.println("reader start : " + start + ", reader end : " + end);

		FlatFileItemReader<LogDTO> reader = new FlatFileItemReader<>();

		// 멀티 리소스를 사용하기 위해 주석처리 함
		reader.setResource(new FileSystemResource(path));
//		reader.setLinesToSkip((int) (start - 1));
//		reader.setMaxItemCount((int) (end - start + 1));

		DefaultLineMapper<LogDTO> lineMapper = new DefaultLineMapper<>() {
			// 파일의 라인 넘버를 로그에 저장해 plantUML 이 순서대로 그려지게 함
			@Override
			public LogDTO mapLine(String line, int lineNumber) throws Exception {
				LogDTO log = super.mapLine(line, lineNumber);
				log.setLineNumber(lineNumber);
				return log;
			}

		};

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(";");
		tokenizer.setNames("timestmp", "loggerName", "levelString", "callerClass", "callerMethod", "traceId", "userId",
				"ipAddress", "device", "executeResult", "query", "uri");
		lineMapper.setLineTokenizer(tokenizer);

		BeanWrapperFieldSetMapper<LogDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(LogDTO.class);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(lineMapper);
//		System.out.println("return reader; start : end >>" + start + " : " + end);
		return reader;
	}

	// 아무 역할도 하지 않는 writer
	public class DummyItemWriter implements ItemWriter<Object> {
		@Override
		public void write(Chunk<? extends Object> chunk) throws Exception {
//			System.out.println("dummy activated");
		}
	}

	@Bean
	public DummyItemWriter dummyWriter() {
		return new DummyItemWriter();
	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingEventWriter() {
//		String sql = "INSERT INTO logging_event (timestmp, logger_name, level_string, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, seq) "
//				+ "VALUES (:timestmp, :loggerName, :levelString, :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device, :executeResult, :lineNumber)";
//
//		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();

		// 성능 개선을 위해 ps 방식으로 변경
		String sql = "INSERT INTO logging_event (timestmp, logger_name, level_string, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, seq) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new eventWriterItemPSSetter()).build();

	}

	public class eventWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {
			ps.setString(1, item.getTimestmp());
			ps.setString(2, item.getLoggerName());
			ps.setString(3, item.getLevelString());
			ps.setString(4, item.getCallerClass());
			ps.setString(5, item.getCallerMethod());
			ps.setString(6, item.getUserId());
			ps.setString(7, item.getTraceId());
			ps.setString(8, item.getIpAddress());
			ps.setString(9, item.getDevice());
			ps.setString(10, item.getExecuteResult());
			ps.setLong(11, item.getLineNumber());

		}

	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingSlowWriter() {
//		String sql = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, query, uri) "
//				+ "VALUES (:timestmp, :callerClass, :callerMethod, :userId, :traceId, :ipAddress, :device,"
//				+ ":executeResult, "
//				+ "CASE WHEN :callerClass = 'SQL' THEN :callerMethod ELSE NULL END, CASE WHEN :callerClass != 'SQL' THEN :callerClass ELSE NULL END)";

//		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();

		// 성능 개선을 위해 ps 방식으로 변경
		String sql = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, user_id, trace_id, ip_address, device, execute_result, query, uri) "
				+ "VALUES(?,?,?,?,?,?,?,?, "
				+ "CASE WHEN ? = 'SQL' THEN ? ELSE NULL END, CASE WHEN ? != 'SQL' THEN ? ELSE NULL END)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new slowWriterItemPSSetter()).build();

	}

	public class slowWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {
			ps.setString(1, item.getTimestmp());
			ps.setString(2, item.getCallerClass());
			ps.setString(3, item.getCallerMethod());
			ps.setString(4, item.getUserId());
			ps.setString(5, item.getTraceId());
			ps.setString(6, item.getIpAddress());
			ps.setString(7, item.getDevice());
			ps.setString(8, item.getExecuteResult());
			ps.setString(9, item.getCallerClass());
			ps.setString(10, item.getCallerMethod());
			ps.setString(11, item.getCallerClass());
			ps.setString(12, item.getCallerClass());

		}

	}

	@Bean
	public JdbcBatchItemWriter<LogDTO> loggingErrorWriter() {
//		String sql = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query, uri, execute_result) "
//				+ "VALUES (:timestmp, :userId, :traceId, :ipAddress, :device, :callerClass, :callerMethod, "
//				+ ":query, :uri, :executeResult)";
//
//		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql).beanMapped().build();

		// 성능 개선을 위해 ps 방식으로 변경
		String sql = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query, uri, execute_result) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new errorWriterItemPSSetter()).build();
	}

	public class errorWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {
			ps.setString(1, item.getTimestmp());
			ps.setString(2, item.getUserId());
			ps.setString(3, item.getTraceId());
			ps.setString(4, item.getIpAddress());
			ps.setString(5, item.getDevice());
			ps.setString(6, item.getCallerClass());
			ps.setString(7, item.getCallerMethod());
			ps.setString(8, item.getQuery());
			ps.setString(9, item.getUri());
			ps.setString(10, item.getExecuteResult());

		}

	}

	@Bean
	public CompositeItemWriter<LogDTO> compositeWriter() {
		CompositeItemWriter<LogDTO> writer = new CompositeItemWriter<>();
		writer.setDelegates(List.of(conditionalEventWriter(), conditionalErrorWriter(), conditionalSlowWriter()));

		return writer;
	}

	@Bean
	public ItemWriter<LogDTO> conditionalEventWriter() {

		return items -> {
			for (LogDTO log : items) {
				if ("trace".equalsIgnoreCase(log.getLoggerName()) || "slow".equalsIgnoreCase(log.getLoggerName())
						|| "error".equalsIgnoreCase(log.getLoggerName())) {
					loggingEventWriter().write(new Chunk<>(List.of(log)));
				}
			}
		};
	}

	@Bean
	public ItemWriter<LogDTO> conditionalSlowWriter() {

		return items -> {
			for (LogDTO log : items) {
				if ("slow".equalsIgnoreCase(log.getLoggerName())) {
					loggingSlowWriter().write(new Chunk<>(List.of(log)));
				}
			}
		};
	}

	@Bean
	public ItemWriter<LogDTO> conditionalErrorWriter() {

		return items -> {
			for (LogDTO log : items) {
				if ("error".equalsIgnoreCase(log.getLoggerName())) {
					try {
						loggingErrorWriter().write(new Chunk<>(List.of(log)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

}
