package com.neo.gatewayserver.dto;

import lombok.Data;

@Data
public class LogDTO {

	private String timestmp;
	private String loggerName;
	private String levelString;
	private String callerClass;
	private String callerMethod;
	private String query;
	private String uri;
	private String traceId;
	private String userId;
	private String ipAddress;
	private String device;
	private String executeResult;
	private String serverId = "BT20";
	private long lineNumber;

	
}
