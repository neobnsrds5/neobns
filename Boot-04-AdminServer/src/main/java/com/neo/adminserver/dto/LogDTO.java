package com.neo.adminserver.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

@Alias("LogDTO")
@Getter
@Setter
@ToString
public class LogDTO {

	private String eventId;
	private String timestmp;
	private String formattedMessage;
	private String loggerName;
	private String levelString;
	private String threadName;
	private String referenceFlag;
	private String callerFilename;
	private String callerClass;
	private String callerMethod;
	private String query;
	private String uri;
	private String traceId;
	private String userId;
	private String ipAddress;
	private String device;
	private String executeResult;

	// 검색을 위한 변수 추가
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;
}
