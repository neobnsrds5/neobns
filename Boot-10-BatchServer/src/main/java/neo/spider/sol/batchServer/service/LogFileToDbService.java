package neo.spider.sol.batchServer.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.websocket.server.ServerEndpoint;

@Service
public class LogFileToDbService {

	// 배치 사이즈
	private static final int BatchSize = 100;

	// 디비 접속 데이터
	@Value("${spring.datasource-data.url}")
	private String databaseUrl;
	@Value("${spring.datasource-data.username}")
	private String user = "POC_USER";
	@Value("${spring.datasource-data.password}")
	private String password = "neobns1!";

	// 로깅 sql
	private final String eventSql = "INSERT INTO logging_event (timestmp, logger_name, level_string, caller_class, caller_method, trace_id, user_id,  ip_address, device, execute_result, seq) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	private final String slowSql = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, trace_id, user_id, ip_address, device, execute_result, query, uri) "
			+ "VALUES(?,?,?,?,?,?,?,?, "
			+ "CASE WHEN ? = 'SQL' THEN ? ELSE NULL END, CASE WHEN ? != 'SQL' THEN ? ELSE NULL END)";
	private final String errorSql = "INSERT INTO logging_error (timestmp, trace_id, user_id,  ip_address, device, caller_class, caller_method, query, uri, execute_result) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?)";

	// prepareStatement 생성
	private PreparedStatement preparedStatement(Connection connection, String sql) {

		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}

	// sql문 순서에 맞춘 ps 세팅
	private void setPreparedStatement(PreparedStatement ps, String[] tokens, int[] indices) throws SQLException {

		for (int i = 0; i < indices.length; i++) {
			ps.setString(i + 1, tokens[indices[i]]);
		}
	}

	// event 테이블용 ps 세팅
	private PreparedStatement setEventPs(PreparedStatement eventPs, String[] tokens) throws SQLException {

		// timestmp, logger name, levelstring, caller class, caller method, trace id,
		// user id, ip address, device, execute_result
		int[] indices = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		setPreparedStatement(eventPs, tokens, indices);

		return eventPs;
	}

	// slow 테이블용 ps 세팅
	private PreparedStatement setSlowPs(PreparedStatement slowPs, String[] tokens) throws SQLException {

		// timestmp, aller class, caller method, trace id, user id, ip address, device,
		// execute_result, aller class, query, caller class, uri
		int[] indices = { 0, 3, 4, 5, 6, 7, 8, 9, 3, 4, 3, 3 };
		setPreparedStatement(slowPs, tokens, indices);

		return slowPs;
	}

	// error 테이블용 ps 세팅
	private PreparedStatement setErrorPs(PreparedStatement errorPs, String[] tokens) throws SQLException {

		// timestmp, trace id, user id, ip address, device, caller class, caller method,
		// query, uri, execute_result
		int[] indices = { 0, 5, 6, 7, 8, 3, 4, 10, 11, 9 };
		setPreparedStatement(errorPs, tokens, indices);

		return errorPs;
	}

	// ps, 커넥션 닫기
	private void closeConnection(Connection connection, PreparedStatement... statements) throws SQLException {

		for (PreparedStatement ps : statements) {
			ps.close();
		}
		connection.close();
	}

	// 스트링 토큰 정리
	private String[] getTokens(String line) {
		// 한 row를 ;로 나눈 다음 null인 경우 빈 스트링으로 변환한 후 공백 제거
		return Arrays.stream(line.split(";")).map(token -> token == null ? "" : token.trim()).toArray(String[]::new);
	}

	// 배치에서 호출할 메서드
	public void executeLogFileToDb(String filePath) throws SQLException {

		long start = System.currentTimeMillis();
		System.out.println("시작 : " + start);
		Connection connection = DriverManager.getConnection(databaseUrl, user, password);
		PreparedStatement eventPs = preparedStatement(connection, eventSql);
		PreparedStatement slowPs = preparedStatement(connection, slowSql);
		PreparedStatement errorPs = preparedStatement(connection, errorSql);
		try (LineNumberReader lineReader = new LineNumberReader(new FileReader(filePath));) {
			connection.setAutoCommit(false);
			String line;
			int eventCount = 0, slowCount = 0, errorCount = 0;

			while ((line = lineReader.readLine()) != null) {

				String[] tokens = getTokens(line);

				System.out.println("tokens.toString() : " + Arrays.toString(tokens));

				System.out.println("tokens[1] : " + tokens[1] + tokens[1].equals("TRACE"));

				// 로거 네임으로 분류
				switch (tokens[1]) {
				case "TRACE":

					// logging event
					eventPs = setEventPs(eventPs, tokens);
					System.out.println("lineReader.getLineNumber() : " + lineReader.getLineNumber());
					// query
					eventPs.setInt(11, lineReader.getLineNumber());
					eventCount++;
					System.out.println("eventCount++; " + eventCount);
					eventPs.addBatch();
					break;

				case "SLOW":

					// logging slow
					slowPs = setSlowPs(slowPs, tokens);
					slowCount++;
					System.out.println("slowcount : " + slowCount);
					slowPs.addBatch();
					break;

				case "ERROR":

					// logging error
					errorPs = setErrorPs(errorPs, tokens);
					errorCount++;
					errorPs.addBatch();
					break;

				default:
					break;
				}

				// 100개 도달 시 배치 실행
				executeBatches(eventCount, slowCount, errorCount, eventPs, slowPs, errorPs, connection);

				System.out.println("counts : " + eventCount + " : " + slowCount + " : " + errorCount);

			}

			finalizeBatches(eventCount, slowCount, errorCount, eventPs, slowPs, errorPs, connection);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 연결 닫기
			closeConnection(connection, eventPs, slowPs, errorPs);
		}

		long end = System.currentTimeMillis();
		System.out.println("끝 : " + end);
		System.out.println("경과시간 : " + ((end - start) / 1000) + "s");
	}

	// 100개 도달 시 배치 실행
	private void executeBatch(int count, PreparedStatement preparedStatement, Connection connection)
			throws SQLException {

		if (count > 0 && count % BatchSize == 0) {
			preparedStatement.executeBatch();
			connection.commit();
		}
	}

	// 100개 도달하는 배치들 실행
	private void executeBatches(int eventCount, int slowCount, int errorCount, PreparedStatement eventPs,
			PreparedStatement slowPs, PreparedStatement errorPs, Connection connection) throws SQLException {

		// 이벤트 테이블에 적재
		executeBatch(eventCount, eventPs, connection);
		// 슬로우 테이블에 적재
		executeBatch(slowCount, slowPs, connection);
		// 에러 테이블에 적재
		executeBatch(errorCount, errorPs, connection);

	}

	private void finalizeBatch(int count, PreparedStatement ps, Connection connection) throws SQLException {
		if (count % BatchSize != 0) {
			ps.executeBatch();
			connection.commit();
		}
	}

	// 100개를 채우지 못한 나머지 데이터
	private void finalizeBatches(int eventCount, int slowCount, int errorCount, PreparedStatement eventPs,
			PreparedStatement slowPs, PreparedStatement errorPs, Connection connection) throws SQLException {

		// 마지막 event 배치 커밋
		finalizeBatch(eventCount, eventPs, connection);
		// 마지막 slow 배치 커밋
		finalizeBatch(slowCount, slowPs, connection);
		// 마지막 error 배치 커밋
		finalizeBatch(errorCount, errorPs, connection);

	}

}
