package com.neo.adminserver.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Alias("LogDTO")
@Getter
@Setter
@ToString
public class LogDTO {

	private String timestmp; // 로그 찍힌 시간
	private String traceId;
	private String requestURL;
	private String userId;
	private String userIp;
	private String device;
	private String calledBy;
	private String current;
	private Long callTimestmp; // 메서드 호출 시간, 요청이 서버에 입장한 시간...
	private Long executionTime;
	private String responseStatus;
	private String error;
	private String delay;
	private String query;

	// 검색 파라미터 저장용
	private String port;
	private String executeResult;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;

	//mybatis 검색 파라미터용
	private Long ltCallTimestamp;
	private Long gtCallTimestamp;
	private Long searchExecuteTime;
}
