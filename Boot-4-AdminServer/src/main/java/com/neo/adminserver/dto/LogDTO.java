package com.neo.adminserver.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("LogDTO")
@Getter
@Setter
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
}