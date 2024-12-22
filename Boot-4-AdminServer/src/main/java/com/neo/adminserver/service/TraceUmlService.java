package com.neo.adminserver.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import com.neo.adminserver.dto.LogDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class TraceUmlService {

	public static String buildUmlList(List<LogDTO> logList) {

		List<LogDTO> traceList = new ArrayList<LogDTO>();
		List<LogDTO> errorList = new ArrayList<LogDTO>();
		List<LogDTO> slowList = new ArrayList<LogDTO>();

		List<UmlDTO> umlList = new ArrayList<>();
		umlList.add(new UmlDTO("User", "", "black"));

		Set<String> appList = new HashSet<>();
		String user = "";

		for (LogDTO log : logList) {
			switch (log.getLoggerName()) {
			case "TRACE":
				traceList.add(log);
				break;
			case "ERROR":
				errorList.add(log);
				break;
			case "SLOW":
				slowList.add(log);
				break;
			}
		}

		for (int i = 0; i < traceList.size(); i++) {
			LogDTO log = traceList.get(i);
			LogDTO stateLog = null;
			UmlDTO uml = new UmlDTO();
			String callerClass = log.getCallerClass();
			String callerMethod = log.getCallerMethod();
			String executeResult = log.getExecuteResult();

			// HTTP
			if (callerClass.contains("http://")) {
				String[] parts = callerClass.split("/");
				appList.add(parts[3]);
				user = parts[3];

				// ip:port 간단하게 정리
				String port = "\"" + parts[2] + "\"";
				uml.setSource(port);

				// -> 방향인 경우
				if (executeResult == null) {
					// URI 간단하게 정리
					String simpleUrl = "";
					for (int j = 3; j < parts.length; j++) {
						simpleUrl += ("/" + parts[j]);
					}
					uml.setContent(simpleUrl + " : " + callerMethod);
				}
				// <- 방향인 경우
				else {
					String content = "";

					// error인 경우 에러 정보 출력
					stateLog = checkError(log, errorList);
					if (stateLog != null) {
						content += (" [ " + stateLog.getExecuteResult() + " ] ");
						uml.setColor("red");
					}
					// error가 아닌 경우 slow 확인
					else {
						stateLog = checkSlow(log, slowList);
						if (stateLog != null) {
							uml.setColor("orangered");
						}
					}

					content += (" [ " + executeResult + "ms ] ");
					uml.setContent(content);
				}

			}
			// SQL
			else if (callerClass.equals("SQL")) {
				uml.setSource("Database");

				// -> 방향
				if (executeResult == null) {
					continue;
				}
				// <- 방향
				else {
					String content = callerMethod;

					stateLog = checkError(log, errorList);
					if (stateLog != null) {
						content += ("\\n<font color=red>[ " + stateLog.getExecuteResult() + " ] "); // 에러 정보 추가
						uml.setColor("red");
					} else {
						stateLog = checkSlow(log, slowList);
						if (stateLog != null) {
							uml.setColor("orangered");
							content += ("\\n<font color=orangered>[ " + executeResult + "ms ]"); // 소요 시간 추가
						}
					}
					uml.setContent(content);
				}
			}
			// AOP
			else {
				int index = callerClass.lastIndexOf(".");
				uml.setSource(callerClass.substring(index + 1));

				// -> 방향인 경우
				if (executeResult == null) {
					uml.setContent(callerMethod);
				}
				// <- 방향인 경우
				else {
					String content = "";

					// error인 경우 에러 정보 출력
					stateLog = checkError(log, errorList);
					if (stateLog != null) {
						content += (" [ " + stateLog.getExecuteResult() + " ] ");
						uml.setColor("red");
					}
					// error가 아닌 경우 slow 확인
					else {
						stateLog = checkSlow(log, slowList);
						if (stateLog != null) {
							uml.setColor("orangered");
						}
					}

					content += (" [ " + executeResult + "ms ] ");
					uml.setContent(content);
				}
			}
			umlList.add(uml);
		}

		umlList.add(new UmlDTO("User", "", "black"));

		System.out.println("<umlList>");
		for (int i = 0; i < umlList.size(); i++) {
			System.out.println(umlList.get(i).toString());
		}

		return buildUmlDiagram(umlList);
	}

	public static String buildUmlDiagram(List<UmlDTO> umlList) {
		Boolean isReturn = false; // 화살표 방향
		StringBuilder sb = new StringBuilder("autonumber\n");
		sb.append("actor User\n").append("skinparam sequenceArrowThickness 2\n").append("skinparam roundcorner 20\n");

		for (int i = 0; i < umlList.size() - 1; i++) {

			isReturn = (umlList.size() / 2) <= i;
			UmlDTO curUml = umlList.get(i);
			UmlDTO nextUml = umlList.get(i + 1);

			if (curUml.getSource().equals(nextUml.getSource())) {
				continue;
			}
			if (nextUml.getSource().equals("Database")) {
				sb.append(String.format("%s %s %s : <font color=%s> %s\n", curUml.getSource(), "->", curUml.getSource(),
						nextUml.getColor(), nextUml.getContent()));
				nextUml.setSource(curUml.getSource());
			} else {
				if (isReturn) {
					sb.append(String.format("%s %s %s : <font color=%s> %s\n", curUml.getSource(), "->",
							nextUml.getSource(), curUml.getColor(), curUml.getContent()));
				} else { // -> 방향 rest call인 경우
					if (i != 0 && nextUml.getSource().startsWith("\"")) {
						// 가장 최근의 service를 rest service라 가정
						int restCallServiceIndex = 0;
						for (int j = i; j >= 0; j--) {
							if (umlList.get(j).getSource().contains("Service")) {
								restCallServiceIndex = j;
								break;
							}
						}
						UmlDTO restUml = umlList.get(restCallServiceIndex);
						sb.append(String.format("%s %s %s : <font color=%s> %s\n", restUml.getSource(), "->",
								nextUml.getSource(), nextUml.getColor(), nextUml.getContent())); 
					} else {
						sb.append(String.format("%s %s %s : <font color=%s> %s\n", curUml.getSource(), "->",
								nextUml.getSource(), nextUml.getColor(), nextUml.getContent()));
					}
				}
			}
		}

		System.out.println("만들어진 uml string \n" + sb.toString());

		return sb.toString();

	}

	public static LogDTO checkError(LogDTO curLog, List<LogDTO> errorList) {
		for (LogDTO errorLog : errorList) {
			if (errorLog.getCallerClass().equals(curLog.getCallerClass())
					&& errorLog.getCallerMethod().equals(curLog.getCallerMethod())) {
				return errorLog;
			}
		}
		return null;
	}

	public static LogDTO checkSlow(LogDTO curLog, List<LogDTO> slowList) {
		for (LogDTO slowLog : slowList) {
			if (slowLog.getCallerClass().equals(curLog.getCallerClass())
					&& slowLog.getCallerMethod().equals(curLog.getCallerMethod())) {
				return slowLog;
			}
		}
		return null;
	}
}

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
class UmlDTO {
	String source;
	String content;
	String color = "black";
}