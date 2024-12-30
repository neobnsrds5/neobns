package com.neo.plantUMLServer.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

}
