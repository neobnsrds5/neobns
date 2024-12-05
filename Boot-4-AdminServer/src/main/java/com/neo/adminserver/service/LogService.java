package com.neo.adminserver.service;

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
		for (int i = 0; i < newList.size(); i++) {
			if (newList.get(i).getCallerClass().contains("http://")) {
				String newMethod = newList.get(i).getCallerClass() + " : " + newList.get(i).getCallerMethod();
				newList.get(i).setCallerClass("user");
				newList.get(i).setCallerMethod(newMethod);
			} else if (newList.get(i).getCallerClass().contains("com.example.neobns")) {
				int lastDot = newList.get(i).getCallerClass().lastIndexOf(".");
				String editedClass = newList.get(i).getCallerClass().substring(lastDot + 1);
				newList.get(i).setCallerClass(editedClass);
			}

			if (newList.get(i).getLoggerName().equals("ERROR") || newList.get(i).getLoggerName().equals("SLOW")) {
				LogDTO exitedLog = newList.remove(i);
				slowOrErrorList.add(exitedLog);
				i--;
			}
		}

		for (LogDTO dto : newList) {
			System.out.println("<newList>");
			System.out.println(dto);
		}

		for (LogDTO dto : slowOrErrorList) {
			System.out.println("<slowOrErrorList>");
			System.out.println(dto);
		}

		StringBuilder builder = new StringBuilder();
		LogDTO previousLog = null;
		boolean sqlEncountered = false;

		for (int i = 0; i < newList.size(); i++) {

			LogDTO currentLog = newList.get(i);
			String addedString = null;
			if (i == 0 || i == newList.size() - 1) {
				if (i == 0) {
					LogDTO nextLog = newList.get(i + 1);
					addedString = currentLog.getCallerClass() + " -> " + nextLog.getCallerClass() + " : "
							+ currentLog.getCallerMethod();
				} else {
					addedString = currentLog.getCallerClass() + " -> " + newList.get(0).getCallerClass() + " : "
							+ currentLog.getCallerMethod();
				}

			} else {

				if (currentLog.getCallerClass().equals("SQL")) {
					sqlEncountered = true;
					String executeResult = currentLog.getExecuteResult();
					if (executeResult == null) {
						addedString = currentLog.getCallerClass() + " -> " + currentLog.getCallerClass()
								+ " : <font color=red>" + currentLog.getCallerMethod() + " - No Result</font>";
					} else if (executeResult.contains("Exception")) {
						addedString = currentLog.getCallerClass() + " -> " + currentLog.getCallerClass()
								+ " : <font color=red>" + currentLog.getCallerMethod() + " - " + executeResult
								+ "</font>";
					} else {
						addedString = currentLog.getCallerClass() + " -> " + currentLog.getCallerClass() + " : "
								+ currentLog.getCallerMethod() + " , " + executeResult + "ms";
					}
				}

				else if (existsInErrorList(currentLog, slowOrErrorList)) {
					addedString = currentLog.getCallerClass() + " -> " + currentLog.getCallerClass()
							+ " : <font color=red>" + currentLog.getExecuteResult() + "</font>";
				}

				else if (previousLog != null && previousLog.getCallerClass().equals("SQL")) {
					String currentResult = currentLog.getExecuteResult();
					String previousResult = previousLog.getExecuteResult();

					if (isNumeric(previousResult) && isNumeric(currentResult)) {
						long duration = Long.parseLong(currentResult) - Long.parseLong(previousResult);
						addedString = previousLog.getCallerClass() + " -> " + currentLog.getCallerClass() + " : "
								+ currentLog.getCallerMethod() + " - " + duration + "ms";
					} else {
						addedString = previousLog.getCallerClass() + " -> " + currentLog.getCallerClass() + " : "
								+ currentLog.getCallerMethod();
					}
				}

				else if (sqlEncountered && previousLog != null) {
					String currentResult = currentLog.getExecuteResult();
					String previousResult = previousLog.getExecuteResult();

					if (isNumeric(previousResult) && isNumeric(currentResult)) {
						long duration = Long.parseLong(currentResult) - Long.parseLong(previousResult);
						addedString = previousLog.getCallerClass() + " -> " + currentLog.getCallerClass() + " : "
								+ previousLog.getCallerMethod() + " - " + duration + "ms";
					} else {
						addedString = previousLog.getCallerClass() + " -> " + currentLog.getCallerClass() + " : "
								+ previousLog.getCallerMethod();
					}
				}

				else if (!sqlEncountered && previousLog != null) {
					LogDTO nextLog = newList.get(i + 1);
					addedString = currentLog.getCallerClass() + " -> " + nextLog.getCallerClass() + " : "
							+ currentLog.getCallerMethod();
				}

				if (addedString != null) {
					builder.append(addedString).append(System.lineSeparator());
				}

			}

			previousLog = currentLog;
		}

		System.out.println("Generated PlantUML String:");
		System.out.println(builder.toString());

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

	private boolean existsInErrorList(LogDTO currentLog, List<LogDTO> slowOrErrorList) {
		for (LogDTO errorLog : slowOrErrorList) {
			if (errorLog.getCallerClass().equals(currentLog.getCallerClass())
					&& errorLog.getCallerMethod().equals(currentLog.getCallerMethod())
					&& errorLog.getExecuteResult().equals(currentLog.getExecuteResult())) {
				return true;
			}
		}
		return false;
	}

}
