package com.neo.adminserver.service;

import java.time.LocalDateTime;
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

	public List<LogDTO> findSlowByPage(int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findSlowByPage(size, offset);
	}

	public List<LogDTO> findErrorByPage(int page, int size) {
        int offset = (page - 1) * size;
        return logMapper.findErrorByPage(size, offset);
	}

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}
	
	public int countSlowLogs() {
        return logMapper.countSlowLogs();
    }

    public int countErrorLogs() {
        return logMapper.countErrorLogs();
    }
    
    public List<LogDTO> findSlowLogs(
            int page,
            int size,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query) {
        int offset = (page - 1) * size;
        return logMapper.findSlowLogs(startTime, endTime, traceId, userId, ipAddress, query, size, offset);
    }

    public int countSlowSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query) {
        return logMapper.countSlowSearchLogs(startTime, endTime, traceId, userId, ipAddress, query);
    }
    
    public List<LogDTO> findErrorLogs(
            int page,
            int size,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(startTime, endTime, traceId, userId, ipAddress, query, size, offset);
    }

    public int countErrorSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query) {
        return logMapper.countErrorSearchLogs(startTime, endTime, traceId, userId, ipAddress, query);
    }
    
	public List<LogDTO> findByTable(int page, int size, String callerMethod) {
		int offset = (page - 1) * size;
	    return logMapper.findByTable(size, offset, callerMethod);
	}
	
    public int countSQLTable(String callerMethod) {
        return logMapper.countSQLTable(callerMethod);
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

			if (i == 0) {
				String[] parts = newList.get(i).getCallerClass().split("/");
				currentUser = parts[3];
				userList.add(currentUser);
				user = newList.get(i).getCallerClass();
				
				// URI 간단하게 정리
				String simpleUrl = "";
				for(int j=3; j<parts.length; j++) {
					simpleUrl += ("/" + parts[j]);
				}
				
				String newMethod = simpleUrl.toString() + " : " + newList.get(i).getCallerMethod();
				newList.get(i).setCallerClass("user");
				newList.get(i).setCallerMethod(newMethod);
			} else if (newList.get(i).getCallerClass().contains("http://")) {
				
				String[] parts = newList.get(i).getCallerClass().split("/");
				
				// URI 간단하게 정리
				String simpleUrl = "";
				for(int j=3; j<parts.length; j++) {
					simpleUrl += parts[j];
				}

				if (i <= newList.size() / 2) {
					currentUser = parts[3];
					userList.add(currentUser);
				}

				if (newList.get(i).getCallerClass().equals(user)) {
					String newMethod = simpleUrl + " : " + newList.get(i).getCallerMethod();
					newList.get(i).setCallerClass("user");
					newList.get(i).setCallerMethod(newMethod);
				} else {
					String newMethod = simpleUrl + " : " + newList.get(i).getCallerMethod();
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


		StringBuilder builder = new StringBuilder();
		builder.append("autonumber").append(System.lineSeparator());
		builder.append("actor user").append(System.lineSeparator());
		builder.append("skinparam sequenceArrowThickness 2").append(System.lineSeparator());
		builder.append("skinparam roundcorner 20").append(System.lineSeparator());

		// newList

		int sqlCount = 0;
		int newlistSize = newList.size();

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
							+ " : <font color=red> " + errorLog.getCallerMethod() + " - " + errorLog.getExecuteResult();
					
					if (errorLog.getExecuteResult().matches("-?\\d+")) { // 정수인지 확인하는 정규식
						addedString = addedString + "ms";
					} 
				} else {
					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i).getCallerClass() + " : "
							+ newList.get(i).getCallerMethod() + " - " + newList.get(i).getExecuteResult() + "ms";
				}

			} else if (i > newlistSize / 2) {

				if (existsInErrorList(currentLog, slowOrErrorList) != null) {

					LogDTO errorLog = existsInErrorList(currentLog, slowOrErrorList);

					if (errorLog.getLoggerName().equals("ERROR")) {

						addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass()
								+ " : <font color=red>" + errorLog.getExecuteResult();
								
						if(newList.get(i).getExecuteResult() != null) {
							addedString += " - " + newList.get(i).getExecuteResult() + "ms";
						}

					} else if (errorLog.getLoggerName().equals("SLOW")) {
						addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass()
								+ " : <font color=blue> " + errorLog.getExecuteResult() + "ms";
					}

				} else {

					addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass() + " : "
							  + newList.get(i).getExecuteResult() + "ms";

				}

			}

			builder.append(addedString).append(System.lineSeparator());

		}

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
