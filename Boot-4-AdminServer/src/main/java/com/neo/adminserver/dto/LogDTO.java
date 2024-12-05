package com.neo.adminserver.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("LogDTO")
@Getter
@Setter
public class LogDTO {

	private long eventId;
	private String timestmp;
	private String formattedMessage;
	private String loggerName;
	private String levelString;
	private String threadName;
	private int referenceFlag;
	private String callerFilename;
	private String callerClass;
	private String callerMethod;
	private String query;
	private String uri;
	private String traceId;
	private String userId;
	private String ipAddress;
	private String device;
	private long executeResult;
}