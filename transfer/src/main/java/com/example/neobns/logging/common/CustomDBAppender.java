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
            
            String userIp = MDC.get("clientIp").split(",")[0];
            stmt.setString(18, userIp);

            stmt.executeUpdate();
       
            
        } catch (SQLException e) {
            addError("Failed to append log entry to database", e);
            throw e;
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
