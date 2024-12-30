package com.neo;

import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.neo.plantUMLServer.dto.LogDTO;

public class DatabaseUtil {

//	spring.datasource-data.url=jdbc:mariadb://neobns.com:13306/db2
//	spring.datasource-data.username=POC_USER
//	spring.datasource-data.password=1234
//	spring.datasource-data.driver-class-name=org.mariadb.jdbc.Driver

	private Connection connection;

	public Connection getConnection() throws Exception {

		if (connection == null) {
			String url = "jdbc:mariadb://neobns.com:13306/db2";
			String userName = "POC_USER";
			String password = "neobns1!";
			Class.forName("org.mariadb.jdbc.Driver");
			connection = DriverManager.getConnection(url, userName, password);
		}

		return connection;
	}

	public List<LogDTO> getSlowList(int page, int size) throws Exception {

		int offset = (page - 1) * size;

		String sql = "SELECT * FROM logging_slow " + "WHERE 1=1 " + "ORDER BY timestmp " + "LIMIT " + size + " OFFSET "+ offset;
		
		connection = getConnection();
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		List<LogDTO> list = new ArrayList<LogDTO>();
		
		while (resultSet.next()) {
			LogDTO logDTO = new LogDTO();
			logDTO.setTimestmp(resultSet.getString("timestmp"));
			logDTO.setCallerClass(resultSet.getString("caller_class"));
			logDTO.setCallerMethod(resultSet.getString("caller_method"));
			logDTO.setUserId(resultSet.getString("user_id"));
			logDTO.setQuery(resultSet.getString("query"));
			logDTO.setUri(resultSet.getString("uri"));
			logDTO.setTraceId(resultSet.getString("trace_id"));
			logDTO.setExecuteResult(resultSet.getString("execute_result"));
			list.add(logDTO);	
		}
		
		return list;
	}

	public List<LogDTO> getTraceList(String traceId) throws Exception {

		String sql = "SELECT * FROM logging_event " + "WHERE trace_id = '" + traceId
				+ "' AND logger_name IN ('TRACE', 'SLOW', 'ERROR') " + "ORDER BY timestmp";
		
		System.out.println(sql);

		List<LogDTO> list = new ArrayList<LogDTO>();
		
		connection = getConnection();

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				LogDTO logDTO = new LogDTO();
				logDTO.setTimestmp(resultSet.getString("timestmp"));
				logDTO.setTraceId(resultSet.getString("trace_id"));
				logDTO.setUserId(resultSet.getString("user_id"));
				logDTO.setLoggerName(resultSet.getString("logger_name"));
				logDTO.setLevelString(resultSet.getString("level_string"));
				logDTO.setCallerClass(resultSet.getString("caller_class"));
				logDTO.setCallerMethod(resultSet.getString("caller_method"));		
				logDTO.setExecuteResult(resultSet.getString("execute_result"));
				list.add(logDTO);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(list.get(0).toString());

		return list;

	}

}
