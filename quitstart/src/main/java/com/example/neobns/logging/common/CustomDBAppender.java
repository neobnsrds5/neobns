package com.example.neobns.logging.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.MDC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Setter
public class CustomDBAppender extends JDBCAppender {
	
	// 아래 프로퍼티에 대한 setter 메소드가 필요
	// log4j.xml에 param들이 setter 메소드를 통해 매핑 됨
	private String url;
    private String user;
    private String password;
    private String driver;
	
    @Override
    public void activateOptions() {
        // 드라이버 로드
        try {
            if (driver != null) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (ClassNotFoundException e) {
            errorHandler.error("Unable to load JDBC driver: " + driver, e, 0);
        }
    }

	@Override
	public void append(LoggingEvent event) {
		
//		boolean previousAutoCommitState = false;
		
		try (Connection connection = DriverManager.getConnection(url, user, password);){
			
//			// finally에서 원상태로 돌리기 위해 커밋상태값 저장
//			previousAutoCommitState = connection.getAutoCommit();
//			
//			// Auto-commit 활성화 - 트랜잭션 롤백의 영향을 받지 않기 위해
//            connection.setAutoCommit(true);
            
			// 기본 로그 저장 (logging_event)
			boolean loggingEventInserted = saveLoggingEvent(event, connection);

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
			log.error("데이터베이스에 로그 저장 중 에러 발생: ", e);
		} finally {
//			// 이전 Auto-commit 상태 복원
//            try {
//				connection.setAutoCommit(previousAutoCommitState);
//			} catch (SQLException e) {
//				log.error("데이터베이스 커밋 상태 변경 중 에러 발생: ", e);
//			}
		}
	}

    private void fallbackSaveLoggingEvent(LoggingEvent event, Connection connection) {
        String fallbackSQL = "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, user_id, trace_id, device, ip_address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)";

        try (PreparedStatement fallbackStmt = connection.prepareStatement(fallbackSQL)) {
        	
        	String userId = (String) MDC.get("userId");
            String requestId = (String) MDC.get("requestId");
            String userAgent = (String) MDC.get("userAgent");
            String userIp = (String) MDC.get("clientIp");
        	
            fallbackStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));
            fallbackStmt.setString(2,event.getRenderedMessage());
            fallbackStmt.setString(3, event.getLoggerName());
            fallbackStmt.setString(4, event.getLevel().toString());
            fallbackStmt.setString(5, event.getThreadName());
            fallbackStmt.setString(6, userId);
            fallbackStmt.setString(7, requestId);
            fallbackStmt.setString(8, userAgent);
            fallbackStmt.setString(9, userIp);

            fallbackStmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to insert fallback log into logging_event", e);
        }
    }

	/**
     * logging_event 테이블에 로그 삽입
     */
    private boolean saveLoggingEvent(LoggingEvent event, Connection connection) {
    	String sql = "INSERT INTO logging_event (timestmp, formatted_message, logger_name, level_string, thread_name, "
                + "caller_filename, caller_class, caller_method, caller_line, "
                + "user_id, trace_id, device, ip_address, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));
            stmt.setString(2, event.getRenderedMessage());
            stmt.setString(3, event.getLoggerName());
            stmt.setString(4, event.getLevel().toString());
            stmt.setString(5, event.getThreadName());

			// Caller 데이터와 MDC 값 우선순위 처리
            String className = (String) MDC.get("className");
            String methodName = (String) MDC.get("methodName");
            LocationInfo locationInfo = event.getLocationInformation();
            
			stmt.setString(6, locationInfo != null ? locationInfo.getFileName() : null);
	        stmt.setString(7, className);
	        stmt.setString(8, methodName);
	        stmt.setString(9, locationInfo != null ? locationInfo.getLineNumber() : null);

            String userId = (String) MDC.get("userId");
            String requestId = (String) MDC.get("requestId");
            String userAgent = (String) MDC.get("userAgent");
            String userIp = (String) MDC.get("clientIp");
            String executeResult = (String) MDC.get("executeResult");

            stmt.setString(10, userId != null ? userId : "UNKNOWN");
            stmt.setString(11, requestId != null ? requestId : "UNKNOWN");
            stmt.setString(12, userAgent != null ? userAgent : "UNKNOWN");
            stmt.setString(13, userIp != null ? userIp : "UNKNOWN");
            stmt.setString(14, executeResult != null ? executeResult : null);

            // logging_event 테이블에 삽입
            stmt.executeUpdate();            
            return true;

        } catch (SQLException e) {
            log.error("Failed to append log entry to logging_event", e);
            return false;
        }
    }

    /**
     * logging_error 테이블에 에러 정보 삽입
     */
    private void saveErrorLog(LoggingEvent event, Connection connection) {
        String errorLogSQL = "INSERT INTO logging_error (timestmp, user_id, trace_id, ip_address, device, caller_class, caller_method, query, uri, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement errorStmt = connection.prepareStatement(errorLogSQL)) {
            errorStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));

            String userId = (String) MDC.get("userId");
            String traceId = (String) MDC.get("requestId");
            String userIp = (String) MDC.get("clientIp");
            String userAgent = (String) MDC.get("userAgent");
            String className = (String) MDC.get("className");
            String methodName = (String) MDC.get("methodName");
            String queryLog = (String) MDC.get("queryLog");
            String uri = (String) MDC.get("requestUri");
            String errorName = (String) MDC.get("executeResult");
            
            errorStmt.setString(2, userId != null ? userId : "UNKNOWN_USER");
            errorStmt.setString(3, traceId);
            errorStmt.setString(4, userIp);
            errorStmt.setString(5, userAgent);
            errorStmt.setString(6, className);
            errorStmt.setString(7, methodName);
            errorStmt.setString(8, queryLog);
            errorStmt.setString(9, uri);
            errorStmt.setString(10, errorName);

            errorStmt.executeUpdate();
            
            if(queryLog != null) {
            	MDC.remove("queryLog");
            }

        } catch (SQLException e) {
            log.error("Failed to append error log entry to database", e);
        }
    }

    /**
     * logging_slow 테이블에 SLOW 로그 삽입
     */
    private void saveSlowLog(LoggingEvent event, Connection connection) {
        String slowLogSQL = "INSERT INTO logging_slow (timestmp, caller_class, caller_method, query, uri, user_id, trace_id, ip_address, device, execute_result) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement slowStmt = connection.prepareStatement(slowLogSQL)) {
            slowStmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));

            LocationInfo locationInfo = event.getLocationInformation();

            String callerClass = MDC.get("className") != null
                    ? (String) MDC.get("className")
                    : (locationInfo != null ? locationInfo.getClassName() : null);
            String callerMethod = MDC.get("methodName") != null
					? (String) MDC.get("methodName")
					: (locationInfo != null ? locationInfo.getMethodName() : null);
			slowStmt.setString(2, callerClass);
			slowStmt.setString(3, callerMethod);
			
			if(callerClass.equals("SQL")) { // slow query
				slowStmt.setString(4, callerMethod);
				slowStmt.setNull(5, java.sql.Types.VARCHAR);
			}else { // slow page
				slowStmt.setNull(4, java.sql.Types.VARCHAR);
				slowStmt.setString(5, callerClass);
			}

            String userId = (String) MDC.get("userId");
            String requestId = (String) MDC.get("requestId");
            String userAgent = (String) MDC.get("userAgent");
            String userIp = (String) MDC.get("clientIp");
            String executeTime = (String) MDC.get("executeResult");

            slowStmt.setString(6, userId != null ? userId : "UNKNOWN");
            slowStmt.setString(7, requestId != null ? requestId : "UNKNOWN");
            slowStmt.setString(8, userIp != null ? userIp : "UNKNOWN");
            slowStmt.setString(9, userAgent != null ? userAgent : "UNKNOWN");
            slowStmt.setString(10, executeTime);

            slowStmt.executeUpdate();
            
        } catch (SQLException e) {
            log.error("Failed to append slow log entry to database", e);
        }
    }

}
