package com.example.neobns.logging.common;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomDBAppender extends DBAppender {
	
	@Override
    protected String getInsertSQL() {
        // 기본 SQL에 user_id 컬럼 추가
        return "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, "
                + "reference_flag, arg0, arg1, arg2, arg3, caller_filename, caller_class, caller_method, caller_line, user_id, trace_id, device, ip_address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            String requestId = "";
            
            Object[] args = event.getArgumentArray();
            for (int i = 0; i < 4; i++) {
                stmt.setString(7 + i, (args != null && i < args.length) ? args[i].toString() : null);
            }

            // Caller 데이터 매핑
            StackTraceElement callerData = event.getCallerData() != null && event.getCallerData().length > 0
                    ? event.getCallerData()[0]
                    : null;
            stmt.setString(11, callerData != null ? callerData.getFileName() : null);
            stmt.setString(12, callerData != null ? callerData.getClassName() : null);
            stmt.setString(13, callerData != null ? callerData.getMethodName() : null);
            stmt.setString(14, callerData != null ? Integer.toString(callerData.getLineNumber()) : null);

            // MDC에서 userId 가져오기
            String userId = MDC.get("userId");
            stmt.setString(15, (userId != null) ? userId : "UNKNOWN_USER");
            
            requestId = MDC.get("requestId");
            stmt.setString(16, requestId);
            
            String userAgent = MDC.get("userAgent");
            stmt.setString(17, userAgent);
            
            String userIp = MDC.get("clientIp");
            stmt.setString(18, userIp);

            stmt.executeUpdate();
            
         // 로그 레벨이 ERROR인 경우 추가적으로 error_logs 테이블에 저장
            if ("ERROR".equals(event.getLevel().toString())) {
                saveErrorLog(event, connection);
            }
       
            
        } catch (SQLException e) {
            addError("Failed to append log entry to database", e);
            throw e;
        }
    }
	
	private void saveErrorLog(ILoggingEvent event, Connection connection) {
        String errorLogSQL = "INSERT INTO error_logs (timestmp, level_string, logger_name, message, exception, user_id) "
                           + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement errorStmt = connection.prepareStatement(errorLogSQL)) {
            // 현재 시간
            errorStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));

            // 에러 로그 데이터 매핑
            errorStmt.setString(2, event.getLevel().toString());
            errorStmt.setString(3, event.getLoggerName());
            errorStmt.setString(4, event.getFormattedMessage());

            // 예외 메시지 처리
            String exceptionMessage = event.getThrowableProxy() != null 
                ? event.getThrowableProxy().getClassName() + ": " + event.getThrowableProxy().getMessage() 
                : null;
            errorStmt.setString(5, exceptionMessage);

            // MDC에서 사용자 ID 가져오기
            String userId = MDC.get("userId");
            errorStmt.setString(6, userId != null ? userId : "UNKNOWN_USER");

            // DB에 삽입 실행
            errorStmt.executeUpdate();
        } catch (SQLException e) {
            addError("Failed to append error log entry to database", e);
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
