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
                + "user_id, trace_id, device, ip_address, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement stmt) throws SQLException {
        boolean previousAutoCommitState = connection.getAutoCommit();
        try {
            // Auto-commit 활성화 - 트랜잭션 롤백의 영향을 받지 않기 위해
            connection.setAutoCommit(true);

            // 기본 로그 저장 (logging_event)
            boolean loggingEventInserted = saveLoggingEvent(event, stmt);

            // 만약 logging_event 삽입 실패 시 최소 정보로 fallback 처리
            // 추가적으로 logging_error에 에러 정보 저장
            if (!loggingEventInserted) {
                saveErrorLog(event, connection);
                fallbackSaveLoggingEvent(event, connection);
            }
            
            // 로그 레벨이 ERROR인 경우 추가적으로 logging_error 테이블에 저장
 			if ("ERROR".equals(event.getLevel().toString()) && loggingEventInserted) {
 				saveErrorLog(event, connection);
 			}

            // 추가적으로 SLOW 로그 처리
            if ("SLOW".equals(event.getLoggerName())) {
                saveSlowLog(event, connection);
            }

        } catch (SQLException e) {
            addError("Failed to process log entry", e);
            throw e;
        } finally {
            // 이전 Auto-commit 상태 복원
            connection.setAutoCommit(previousAutoCommitState);
        }
    }

    private void fallbackSaveLoggingEvent(ILoggingEvent event, Connection connection) {
        String fallbackSQL = "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, user_id, trace_id, device, ip_address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)";

        try (PreparedStatement fallbackStmt = connection.prepareStatement(fallbackSQL)) {
        	
        	String userId = MDC.get("userId");
            String requestId = MDC.get("requestId");
            String userAgent = MDC.get("userAgent");
            String userIp = MDC.get("clientIp");
        	
            fallbackStmt.setLong(1, event.getTimeStamp());
            fallbackStmt.setString(2,event.getFormattedMessage());
            fallbackStmt.setString(3, event.getLoggerName());
            fallbackStmt.setString(4, event.getLevel().toString());
            fallbackStmt.setString(5, event.getThreadName());
            fallbackStmt.setString(6, userId);
            fallbackStmt.setString(7, requestId);
            fallbackStmt.setString(8, userAgent);
            fallbackStmt.setString(9, userIp);

            fallbackStmt.executeUpdate();
        } catch (SQLException e) {
            addError("Failed to insert fallback log into logging_event", e);
        }
    }

	/**
     * logging_event 테이블에 로그 삽입
     */
    private boolean saveLoggingEvent(ILoggingEvent event, PreparedStatement stmt) {
        try {
            stmt.setLong(1, event.getTimeStamp());
            stmt.setString(2, event.getFormattedMessage());
            stmt.setString(3, event.getLoggerName());
            stmt.setString(4, event.getLevel().toString());
            stmt.setString(5, event.getThreadName());
            stmt.setShort(6, computeReferenceMask(event));

            Object[] args = event.getArgumentArray();
            for (int i = 0; i < 4; i++) {
                stmt.setString(7 + i, (args != null && i < args.length) ? args[i].toString() : null);
            }

			// Caller 데이터와 MDC 값 우선순위 처리
			StackTraceElement callerData = (event.getCallerData() != null && event.getCallerData().length > 0)
					? event.getCallerData()[0]
					: null;

			// MDC 값 우선 사용, 없으면 callerData 사용
			String callerClass = MDC.get("className") != null ? MDC.get("className")
					: (callerData != null ? callerData.getClassName() : null);

			String callerMethod = MDC.get("methodName") != null ? MDC.get("methodName")
					: (callerData != null ? callerData.getMethodName() : null);

			String callerFileName = callerData != null ? callerData.getFileName() : null;
			String callerLineNumber = callerData != null ? Integer.toString(callerData.getLineNumber()) : null;

			stmt.setString(11, callerFileName);
			stmt.setString(12, callerClass);
			stmt.setString(13, callerMethod);
			stmt.setString(14, callerLineNumber);

			String userId = MDC.get("userId");
            String requestId = MDC.get("requestId");
            String userAgent = MDC.get("userAgent");
            String userIp = MDC.get("clientIp");
            String executeResult = MDC.get("executeResult");

            stmt.setString(15, userId != null ? userId : "UNKNOWN");
            stmt.setString(16, requestId != null ? requestId : "UNKNOWN");
            stmt.setString(17, userAgent != null ? userAgent : "UNKNOWN");
            stmt.setString(18, userIp != null ? userIp : "UNKNOWN");
            stmt.setString(19, executeResult);

            // logging_event 테이블에 삽입
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            addError("Failed to append log entry to logging_event", e);
            return false;
        }
    }

    /**
     * logging_error 테이블에 에러 정보 삽입
     */
    private void saveErrorLog(ILoggingEvent event, Connection connection) {
        String errorLogSQL = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query, uri, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement errorStmt = connection.prepareStatement(errorLogSQL)) {
            errorStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));

            String userId = MDC.get("userId");
            String traceId = MDC.get("requestId");
            String userIp = MDC.get("clientIp");
            String userAgent = MDC.get("userAgent");
            String className = MDC.get("className");
            String methodName = MDC.get("methodName");
            String queryLog = MDC.get("queryLog");
            String uri = MDC.get("requestUri");
            String errorName = MDC.get("executeResult");
            errorStmt.setString(2, userId != null ? userId : "UNKNOWN_USER");
            errorStmt.setString(3, traceId);
            errorStmt.setString(4, userIp);
            errorStmt.setString(5, userAgent);
            errorStmt.setString(6, className);
            errorStmt.setString(7, methodName);
            errorStmt.setString(8, queryLog);
            if(queryLog != null) {
            	MDC.remove("queryLog");
            }
            errorStmt.setString(9, uri);
            errorStmt.setString(10, errorName);

            errorStmt.executeUpdate();

        } catch (SQLException e) {
            addError("Failed to append error log entry to database", e);
        }
    }

    /**
     * logging_slow 테이블에 SLOW 로그 삽입
     */
    private void saveSlowLog(ILoggingEvent event, Connection connection) {
        String slowLogSQL = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, query, uri, user_id, trace_id, ip_address, device, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(slowLogSQL)) {
            stmt.setLong(1, event.getTimeStamp());

            StackTraceElement callerData = (event.getCallerData() != null && event.getCallerData().length > 0)
                    ? event.getCallerData()[0]
                    : null;

            String callerClass = MDC.get("className") != null
                    ? MDC.get("className")
                    : (callerData != null ? callerData.getClassName() : null);
            String callerMethod = MDC.get("methodName") != null
					? MDC.get("methodName")
					: (callerData != null ? callerData.getMethodName() : null);
			stmt.setString(2, callerClass);
			stmt.setString(3, callerMethod);
			
			if(callerClass.equals("SQL")) { // slow query
				stmt.setString(4, callerMethod);
				stmt.setNull(5, java.sql.Types.VARCHAR);
			}else { // slow page
				stmt.setNull(4, java.sql.Types.VARCHAR);
				stmt.setString(5, callerClass);
			}

            String userId = MDC.get("userId");
            String requestId = MDC.get("requestId");
            String userAgent = MDC.get("userAgent");
            String userIp = MDC.get("clientIp");
            String executeTime = MDC.get("executeResult");

            stmt.setString(6, userId != null ? userId : "UNKNOWN");
            stmt.setString(7, requestId != null ? requestId : "UNKNOWN");
            stmt.setString(8, userIp != null ? userIp : "UNKNOWN");
            stmt.setString(9, userAgent != null ? userAgent : "UNKNOWN");

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
