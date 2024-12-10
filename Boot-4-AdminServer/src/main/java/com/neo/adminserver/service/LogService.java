package com.neo.adminserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogMapper logMapper;
	
	private Map<String, Function<String, List<LogDTO>>> errorSearchMap;
    private Map<String, Function<String, List<LogDTO>>> slowSearchMap;
    private Map<String, Map<String, Function<String, List<LogDTO>>>> tableSearchMap;
	
    @PostConstruct
    private void initializeMaps() {
        errorSearchMap = Map.of(
    		"traceId", value -> logMapper.findErrorLogsByTraceId(value),
		    "userId", value -> logMapper.findErrorLogsByUserId(value),
		    "ipAddress", value -> logMapper.findErrorLogsByIpAddress(value),
		    "uri", value -> logMapper.findErrorLogsByURI(value),
		    "query", value -> logMapper.findErrorLogsByQuery(value)
        );

        slowSearchMap = Map.of(
            "traceId", value -> logMapper.findSlowLogsByTraceId(value),
            "userId", value -> logMapper.findSlowLogsByUserId(value),
            "ipAddress", value -> logMapper.findSlowLogsByIpAddress(value),
            "uri", value -> logMapper.findSlowLogsByURI(value),
            "query", value -> logMapper.findSlowLogsByQuery(value)
        );

        tableSearchMap = Map.of(
            "logging_error", errorSearchMap,
            "logging_slow", slowSearchMap
        );
    }
    
    public List<LogDTO> searchLogs(String criteria, String value, String table) {
        Map<String, Function<String, List<LogDTO>>> searchMap = tableSearchMap.get(table);
        if (searchMap == null) {
            throw new IllegalArgumentException("Invalid table name: " + table);
        }

        Function<String, List<LogDTO>> searchFunction = searchMap.get(criteria);
        if (searchFunction == null) {
            throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }

        return searchFunction.apply(value);
    }

	public List<LogDTO> findSlowByPage() {
		return logMapper.findSlowByPage();
	}

	public List<LogDTO> findErrorByPage() {
		return logMapper.findErrorByPage();
	}

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}

	public String buildPlantUML(String traceID, List<LogDTO> logList) throws CloneNotSupportedException {
		ArrayList<LogDTO> newList = new ArrayList<>();
		for (LogDTO log : logList) {
			newList.add(log.clone());
		}

		ArrayList<LogDTO> slowOrErrorList = new ArrayList<>();

		String user = "";
		List<String> userList = new ArrayList<String>();
		String currentUser = "";
		String gatewayUrl = "http://localhost:8000/";
		String removedUser = "";

		for (int i = 0; i < newList.size(); i++) {

			System.out.println("userlist : " + userList.toString());

			if (i == 0) {
				String[] parts = newList.get(i).getCallerClass().split("/");
				currentUser = parts[3];
				userList.add(currentUser);
				user = newList.get(i).getCallerClass();
				String newMethod = newList.get(i).getCallerClass() + " : " + newList.get(i).getCallerMethod();
				newList.get(i).setCallerClass("user");
				newList.get(i).setCallerMethod(newMethod);
			} else if (newList.get(i).getCallerClass().contains("http://")) {

				if (i <= newList.size() / 2) {
					String[] parts = newList.get(i).getCallerClass().split("/");
					currentUser = parts[3];
					userList.add(currentUser);
				}

				if (newList.get(i).getCallerClass().equals(user)) {
					System.out.println("current User : " + currentUser);
					newList.get(i).setCallerClass("user");
					String newMethod = newList.get(i).getCallerClass() + " : " + newList.get(i).getCallerMethod();
					newList.get(i).setCallerMethod(newMethod);
				} else {

					System.out.println("current User : " + currentUser);
					String newMethod = newList.get(i).getCallerClass() + " : " + newList.get(i).getCallerMethod();
					newList.get(i).setCallerClass("rest_" + currentUser);
					newList.get(i).setCallerMethod(newMethod);
				}

				if (!userList.isEmpty() && i > newList.size() / 2) {
					removedUser = userList.remove(userList.size() - 1);
					if (!userList.isEmpty()) {
						currentUser = userList.get(userList.size() - 1);
					} else {
						currentUser = "";
					}
				}

			} else if (newList.get(i).getCallerClass().contains("Controller")
					|| newList.get(i).getCallerClass().contains("Service")
					|| newList.get(i).getCallerClass().contains("Mapper")) {
				int lastDot = newList.get(i).getCallerClass().lastIndexOf(".");
				String editedClass = currentUser + "_" + newList.get(i).getCallerClass().substring(lastDot + 1);
				newList.get(i).setCallerClass(editedClass);

			} else if (newList.get(i).getCallerClass().contains("SQL")) {
				String editedClass = currentUser + "_" + "DB";
				newList.get(i).setCallerClass(editedClass);
			}

			if (newList.get(i).getLoggerName().equals("ERROR") || newList.get(i).getLoggerName().equals("SLOW")) {
				LogDTO exitedLog = newList.remove(i);
				slowOrErrorList.add(exitedLog);
				userList.add(removedUser);
				i--;
			}
		}

		slowOrErrorList.remove(slowOrErrorList.size() - 1);

		for (LogDTO dto : newList) {
			System.out.println("<newList>");
			System.out.println(dto);
		}

		for (LogDTO dto : slowOrErrorList) {
			System.out.println("<slowOrErrorList>");
			System.out.println(dto);
		}
		
		


		StringBuilder builder = new StringBuilder();
		builder.append("autonumber").append(System.lineSeparator());
		builder.append("actor user").append(System.lineSeparator());
		builder.append("skinparam sequenceArrowThickness 2").append(System.lineSeparator());
		builder.append("skinparam roundcorner 20").append(System.lineSeparator());

		// newList

		int sqlCount = 0;
		int newlistSize = newList.size();
		System.out.println("newlistSize" + newlistSize);
		System.out.println("newlistSize/2" + newlistSize / 2);

		for (int i = 0; i <= newList.size() - 1; i++) {

			LogDTO currentLog = newList.get(i);

			String addedString = null;

			if (i < newlistSize / 2) {

				if (!newList.get(i).getCallerClass().equals(newList.get(i + 1).getCallerClass())) {
					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i + 1).getCallerClass() + " : "
							+ newList.get(i).getCallerMethod();
				} else {
					continue;
				}

			} else if (i == newlistSize / 2) {

				if (existsInErrorList(currentLog, slowOrErrorList) != null) {
					LogDTO errorLog = existsInErrorList(currentLog, slowOrErrorList);
					addedString = errorLog.getCallerClass() + " -> " + errorLog.getCallerClass()
							+ " : <font color=red> " + errorLog.getCallerMethod() + " - " + errorLog.getExecuteResult()
							+ "ms";
				} else {
					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i).getCallerClass() + " : "
							+ newList.get(i).getCallerMethod() + " - " + newList.get(i).getExecuteResult() + "ms";
				}

			} else if (i > newlistSize / 2) {

				long intervalTime = Long.parseLong(newList.get(i).getExecuteResult())
						- Long.parseLong(newList.get(i - 1).getExecuteResult());

				if (existsInErrorList(currentLog, slowOrErrorList) != null) {

					LogDTO errorLog = existsInErrorList(currentLog, slowOrErrorList);

					if (errorLog.getLoggerName().equals("ERROR")) {

						addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass()
								+ " : <font color=red>" + errorLog.getExecuteResult() + " - " + intervalTime + "ms"
								+ "(Total : " + newList.get(i).getExecuteResult() + "ms)";

					} else if (errorLog.getLoggerName().equals("SLOW")) {
						addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass()
								+ " : <font color=red> " + errorLog.getCallerMethod() + " - "
								+ errorLog.getExecuteResult() + "ms";
					}

				} else {

					addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass() + " : "
							+ intervalTime + "ms" + "(Total : " + newList.get(i).getExecuteResult() + "ms)";

				}

			}

			builder.append(addedString).append(System.lineSeparator());

		}

		System.out.println("최종결과 :  \n" + builder.toString());

		return builder.toString();

	}

	private boolean isNumeric(String executeResult) {

		if (executeResult == null) {
			return false;
		}

		try {
			Long.parseLong(executeResult);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}

	}

	private LogDTO existsInErrorList(LogDTO currentLog, List<LogDTO> slowOrErrorList) {
		for (LogDTO errorLog : slowOrErrorList) {
			if (errorLog.getCallerClass().equals(currentLog.getCallerClass())
					&& errorLog.getCallerMethod().equals(currentLog.getCallerMethod())) {
				return errorLog;
			}
		}
		return null;
	}

}
