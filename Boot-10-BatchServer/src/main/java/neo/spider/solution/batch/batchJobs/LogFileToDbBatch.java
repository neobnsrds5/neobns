package neo.spider.solution.batch.batchJobs;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import neo.spider.solution.batch.dto.LogDTO;
import neo.spider.solution.batch.service.FileMaintenanceService;

@Configuration
public class LogFileToDbBatch {
	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final CustomBatchJobListener listener;
	private String path = "../logs/application.log";
	private int chunkSize = 200;

	public LogFileToDbBatch(@Qualifier("dataDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, CustomBatchJobListener listener) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.listener = listener;
	}

	@Bean
	public Job logToDBJob() {
		return new JobBuilder("logToDBJob", jobRepository).listener(listener).start(logToDBStep()).build();
	}

	@Bean
	public Step logToDBStep() {

		return new StepBuilder("logToDBStep", jobRepository).<LogDTO, LogDTO>chunk(chunkSize, transactionManager)
				.reader(logReader()).writer(conditionalWriters()).taskExecutor(logToDBTaskExecutor()).build();
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
	public TaskExecutor syncTaskExecutor() {
		// 단일 스레드로 실행
		return new SyncTaskExecutor();
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

	@Bean
	public FlatFileItemReader<LogDTO> logReader() {

		FlatFileItemReader<LogDTO> reader = new FlatFileItemReader<>();

		reader.setResource(new FileSystemResource(path));

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
		return reader;
	}

	@Bean
	public ItemWriter<LogDTO> loggingEventWriter() {

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
	public ItemWriter<LogDTO> loggingSlowWriter() {

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
	public ItemWriter<LogDTO> loggingErrorWriter() {

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
	public ItemWriter<LogDTO> conditionalWriters() {

		return items -> {

			List<LogDTO> eventList = new ArrayList<>();
			List<LogDTO> delayList = new ArrayList<>();
			List<LogDTO> errorList = new ArrayList<>();

			for (LogDTO log : items) {

				// 이벤트 추가
				eventList.add(log);

				if ("slow".equalsIgnoreCase(log.getLoggerName())) {
					delayList.add(log);
				} else if ("error".equalsIgnoreCase(log.getLoggerName())) {
					errorList.add(log);
				}
			}

			Chunk<LogDTO> chunk = new Chunk<LogDTO>(eventList);
			System.out.println("test chunk : " + chunk.size());

			loggingEventWriter().write(new Chunk<LogDTO>(eventList));
			loggingSlowWriter().write(new Chunk<LogDTO>(delayList));
			loggingErrorWriter().write(new Chunk<LogDTO>(errorList));

			eventList.clear();
			delayList.clear();
			errorList.clear();

		};
	}

}
