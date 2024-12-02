package com.neo.gatewayserver.logging;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomDBAppender extends DBAppender {

	@Override
	protected String getInsertSQL() {
		return "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, "
				+ "reference_flag, arg0, arg1, arg2, arg3, caller_filename, caller_class, caller_method, caller_line, "
				+ "user_id, trace_id, device, ip_address, execute_time) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement stmt) throws SQLException {
		try {
			// 기본 필드 매핑
			stmt.setLong(1, event.getTimeStamp());
			stmt.setString(2, event.getFormattedMessage());
			stmt.setString(3, event.getLoggerName());
			stmt.setString(4, event.getLevel().toString());
			stmt.setString(5, event.getThreadName());
			stmt.setShort(6, computeReferenceMask(event));
			
			// Argument 처리 (최대 4개, 부족하면 null로 채움)
			Object[] args = event.getArgumentArray();
			for (int i = 0; i < 4; i++) {
				stmt.setString(7 + i, (args != null && i < args.length) ? args[i].toString() : null);
			}

			// Caller 데이터와 MDC 값 우선순위 처리
			StackTraceElement callerData = (event.getCallerData() != null && event.getCallerData().length > 0)
			        ? event.getCallerData()[0]
			        : null;

			// MDC 값 우선 사용, 없으면 callerData 사용
			String callerClass = MDC.get("className") != null
			        ? MDC.get("className")
			        : (callerData != null ? callerData.getClassName() : null);

			String callerMethod = MDC.get("methodName") != null
			        ? MDC.get("methodName")
			        : (callerData != null ? callerData.getMethodName() : null);

			String callerFileName = callerData != null ? callerData.getFileName() : null;
			String callerLineNumber = callerData != null ? Integer.toString(callerData.getLineNumber()) : null;

			stmt.setString(11, callerFileName);
			stmt.setString(12, callerClass);
			stmt.setString(13, callerMethod);
			stmt.setString(14, callerLineNumber);

			// MDC에서 데이터 가져오기
			String userId = MDC.get("userId");
			String requestId = MDC.get("requestId");
			String userAgent = MDC.get("userAgent");
			String userIp = MDC.get("clientIp");
			String executeTime = MDC.get("executeTime");
			
			stmt.setString(15, userId != null ? userId : "UNKNOWN");
			stmt.setString(16, requestId != null ? requestId : "UNKNOWN");
			stmt.setString(17, userAgent != null ? userAgent : "UNKNOWN");
			stmt.setString(18, userIp != null ? userIp : "UNKNOWN");
			
			// setLong은 null 처리 불가능하므로 아래와 같은 방식으로 구현
			if (executeTime != null && !executeTime.isEmpty()) {
			    stmt.setLong(19, Long.parseLong(executeTime));
			} else {
			    stmt.setNull(19, java.sql.Types.BIGINT);
			}

			stmt.executeUpdate();

			// 로그 레벨이 ERROR인 경우 추가적으로 logging_error 테이블에 저장
			if ("ERROR".equals(event.getLevel().toString())) {
				saveErrorLog(event, connection);
			}
			// 로그 이름이 SLOW인 경우 추가적으로 logging_slow 테이블에 저장
			if("SLOW".equals(event.getLoggerName().toString())) {
				saveSlowLog(event, connection);
			}

		} catch (SQLException e) {
			addError("Failed to append log entry to database", e);
			throw e;
		}
	}
	
	private void saveErrorLog(ILoggingEvent event, Connection connection) {
        
        String errorLogSQL = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query_log, uri, error_name) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement errorStmt = connection.prepareStatement(errorLogSQL)) {
            // 현재 시간
            errorStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));

            String userId = MDC.get("userId");
            errorStmt.setString(2, userId != null ? userId : "UNKNOWN_USER");
            
            String traceId = MDC.get("requestId");
            errorStmt.setString(3, traceId);
            
            String userIp = MDC.get("clientIp");
            errorStmt.setString(4, userIp);
            
            String userAgent = MDC.get("userAgent");
            errorStmt.setString(5, userAgent);
            
            // Caller 데이터 매핑
            StackTraceElement callerData = event.getCallerData() != null && event.getCallerData().length > 0
                    ? event.getCallerData()[0]
                    : null;
            errorStmt.setString(6, callerData != null ? callerData.getClassName() : null);
            errorStmt.setString(7, callerData != null ? callerData.getMethodName() : null);
            
            System.out.println("callerData.getClassName() : " + callerData.getClassName());
            
            System.out.println("event.getThrowableProxy().getClassName() : " + event.getThrowableProxy().getClassName());
            
            String queryLog = MDC.get("queryLog");
            System.out.println("queryLog : " + queryLog);
            errorStmt.setString(8, queryLog);
            
            String uri = MDC.get("requestUri");
            errorStmt.setString(9, uri);
            
            String errorName = MDC.get("errorName");
            errorStmt.setString(10, errorName);

            // DB에 삽입 실행
            errorStmt.executeUpdate();
        } catch (SQLException e) {
            addError("Failed to append error log entry to database", e);
        }
    }

	private void saveSlowLog(ILoggingEvent event, Connection connection) {
		String slowLogSQL = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, query, uri, user_id, trace_id, ip_address, device, execute_time) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(slowLogSQL)) {
			
			stmt.setLong(1, event.getTimeStamp());

			// Caller 데이터와 MDC 값 우선순위 처리
			StackTraceElement callerData = (event.getCallerData() != null && event.getCallerData().length > 0)
					? event.getCallerData()[0]
					: null;
			
			// MDC 값 우선 사용, 없으면 callerData 사용
			String callerClass = MDC.get("className") != null
					? MDC.get("className")
					: (callerData != null ? callerData.getClassName() : null);

			String callerMethod = MDC.get("methodName") != null
					? MDC.get("methodName")
					: (callerData != null ? callerData.getMethodName() : null);

			stmt.setString(2, callerClass);
			stmt.setString(3, callerMethod);
			
			// TODO: query, uri 저장 필요...!
			stmt.setNull(4, java.sql.Types.VARCHAR);
			stmt.setNull(5, java.sql.Types.VARCHAR);

			// MDC에서 데이터 가져오기
			String userId = MDC.get("userId");
			String requestId = MDC.get("requestId");
			String userAgent = MDC.get("userAgent");
			String userIp = MDC.get("clientIp");
			String executeTime = MDC.get("executeTime");

			stmt.setString(6, userId != null ? userId : "UNKNOWN");
			stmt.setString(7, requestId != null ? requestId : "UNKNOWN");
			stmt.setString(8, userAgent != null ? userAgent : "UNKNOWN");
			stmt.setString(9, userIp != null ? userIp : "UNKNOWN");

			// setLong은 null 처리 불가능하므로 아래와 같은 방식으로 구현
			if (executeTime != null && !executeTime.isEmpty()) {
				stmt.setLong(10, Long.parseLong(executeTime));
			} else {
				stmt.setNull(10, java.sql.Types.BIGINT);
			}

			stmt.executeUpdate();
		} catch (SQLException e) {
			addError("Failed to append slow log entry to database", e);
		}
	}

	private short computeReferenceMask(ILoggingEvent event) {
		short mask = 0;
		if (event.getMDCPropertyMap() != null && !event.getMDCPropertyMap().isEmpty()) {
			mask |= 1;
		}
		if (event.getThrowableProxy() != null) {
			mask |= 2;
		}
		return mask;
	}

}
