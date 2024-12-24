package com.example.neobns.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogDTO {
	
	private long event_id;
	private LocalDateTime timestmp;
	private String user_id;
    private String trace_id;
    private String ip_address;
    private String device;
    private String caller_class;
    private String caller_method;
    private String query;
    private String uri;
    private String execute_result;
}
