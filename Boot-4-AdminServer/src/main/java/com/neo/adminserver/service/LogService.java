package com.neo.adminserver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.logging.LoggersEndpoint.GroupLoggerLevelsDescriptor;
import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import ch.qos.logback.core.joran.conditional.ElseAction;
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

		// newList

		int sqlCount = 0;
		int newlistSize = newList.size();
		System.out.println("newlistSize" + newlistSize);
		System.out.println("newlistSize/2" + newlistSize / 2);

		for (int i = 0; i <= newList.size() - 1; i++) {

			LogDTO currentLog = newList.get(i);

			String addedString = null;

			if (i < newlistSize / 2) {

//				if (existsInErrorList(currentLog, slowOrErrorList) != null) {
//
//					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i + 1).getCallerClass()
//							+ " : <font color=red>" + newList.get(i).getCallerMethod() + " , "
//							+ newList.get(i).getExecuteResult();
//
//				} else {
					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i + 1).getCallerClass() + " : "
							+ newList.get(i).getCallerMethod();
//				}

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

					addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass()
							+ " : <font color=red>" + errorLog.getExecuteResult() + " - " + intervalTime + "ms";

				} else {

					addedString = newList.get(i - 1).getCallerClass() + " -> " + newList.get(i).getCallerClass() + " : "
							+ intervalTime + "ms";

				}

			}

			builder.append(addedString).append(System.lineSeparator());

		}

		// errorslowList

//		for (int i = 0; i <= slowOrErrorList.size() - 2; i++) {
//
//			String addedString2 = null;
//			LogDTO log = slowOrErrorList.get(i);
//			String loggerName = log.getLoggerName();
//
//			if (loggerName.equals("SLOW")) {
//
//				addedString2 = log.getCallerClass() + " -> " + log.getCallerClass() + " : <font color=red> "
//						+ slowOrErrorList.get(i).getCallerMethod() + " - " + slowOrErrorList.get(i).getExecuteResult()
//						+ "ms";
//
//			} else if (loggerName.equals("ERROR")) {
//
//				addedString2 = log.getCallerClass() + " -> " + log.getCallerClass() + " : <font color=red>"
//						+ log.getExecuteResult();
//			}
//
//			builder.append(addedString2).append(System.lineSeparator());
//
//		}

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
