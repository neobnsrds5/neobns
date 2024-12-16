package com.neo.adminserver.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

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
            String uri,
            String executeResult) {
        int offset = (page - 1) * size;
        return logMapper.findSlowLogs(startTime, endTime, traceId, userId, ipAddress, uri, executeResult, size, offset);
    }

    public int countSlowSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String uri,
            String executeResult) {
        return logMapper.countSlowSearchLogs(startTime, endTime, traceId, userId, ipAddress, uri, executeResult);
    }
    
    public List<LogDTO> findErrorLogs(
            int page,
            int size,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query,
            String uri) {
        int offset = (page - 1) * size;
        return logMapper.findErrorLogs(startTime, endTime, traceId, userId, ipAddress, query, uri, size, offset);
    }

    public int countErrorSearchLogs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String traceId,
            String userId,
            String ipAddress,
            String query,
            String uri) {
        return logMapper.countErrorSearchLogs(startTime, endTime, traceId, userId, ipAddress, query, uri);
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
					simpleUrl += ("/" + parts[j]);
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


		StringBuilder umlStringBuilder = new StringBuilder();
		umlStringBuilder.append("autonumber").append(System.lineSeparator());
		umlStringBuilder.append("actor user").append(System.lineSeparator());
		umlStringBuilder.append("skinparam sequenceArrowThickness 2").append(System.lineSeparator());
		umlStringBuilder.append("skinparam roundcorner 20").append(System.lineSeparator());

		// newList

		int sqlCount = 0;
		int newlistSize = newList.size();

		for (int i = 0; i <= newList.size() - 1; i++) {

			LogDTO currentLog = newList.get(i);

			StringBuilder addedString = new StringBuilder();

			if (i < newlistSize / 2) {

				if (!newList.get(i).getCallerClass().equals(newList.get(i + 1).getCallerClass())) {					
					addedString.append(newList.get(i).getCallerClass())
								.append(" -> ")
								.append(newList.get(i + 1).getCallerClass())
								.append(" : ")
								.append(newList.get(i).getCallerMethod());
				} else {
					continue;
				}

			} else if (i == newlistSize / 2) {

				if (existsInErrorList(currentLog, slowOrErrorList) != null) {
					LogDTO errorLog = existsInErrorList(currentLog, slowOrErrorList);
					
					addedString.append(errorLog.getCallerClass()).append(" -> ").append(errorLog.getCallerClass());

					if (errorLog.getLoggerName().equals("ERROR")) {
						addedString.append(" : <font color=red> "); // error이면 빨간색
					} else {
						addedString.append(" : <font color=orangered> "); // slow면 주황색
					}
					addedString.append(errorLog.getCallerMethod());
					
					if(errorLog.getExecuteResult() != null) {
						addedString.append(" [ ").append(errorLog.getExecuteResult());
						
						if (errorLog.getExecuteResult().matches("-?\\d+")) { // 정수인지 확인하는 정규식
							addedString.append("ms ]");
						} else {
							addedString.append(" ]");
						}
					}
								
				} else {
					addedString.append(newList.get(i).getCallerClass())
								.append(" -> ")
								.append(newList.get(i).getCallerClass())
								.append(" : <font color=red> ")
								.append(newList.get(i).getCallerMethod());
					
					if(newList.get(i).getExecuteResult() != null) {
						addedString.append(" [ ")
									.append(newList.get(i).getExecuteResult())
									.append("ms ]");
					}
				}

			} else if (i > newlistSize / 2) {

				if (existsInErrorList(currentLog, slowOrErrorList) != null) {

					LogDTO errorLog = existsInErrorList(currentLog, slowOrErrorList);
					
					addedString.append(newList.get(i - 1).getCallerClass())
								.append(" -> ")
								.append(newList.get(i).getCallerClass());

					if (errorLog.getLoggerName().equals("ERROR")) {

						addedString.append(" : <font color=red> ").append(errorLog.getExecuteResult());
								
						if(newList.get(i).getExecuteResult() != null) {
							addedString.append(" [ " + newList.get(i).getExecuteResult()).append("ms ]");
						}

					} else if (errorLog.getLoggerName().equals("SLOW")) {
						addedString.append(" : <font color=orangered> ")
									.append(" [ ")
									.append(errorLog.getExecuteResult())
									.append("ms ]");
					}

				} else {
					
					addedString.append(newList.get(i - 1).getCallerClass())
								.append(" -> ")
								.append(newList.get(i).getCallerClass())
								.append(" : [ ")
								.append(newList.get(i).getExecuteResult())
								.append("ms ]");

				}

			}

			umlStringBuilder.append(addedString).append(System.lineSeparator());

		}

		return umlStringBuilder.toString();

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
