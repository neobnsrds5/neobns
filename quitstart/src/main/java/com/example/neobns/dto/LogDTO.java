package com.example.neobns.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogDTO {

	private String timestmp;
	private String formatted_message;
	private String logger_name;
	private String level_string;
	private String thread_name;
	private String arg0;
	private String arg1;
	private String arg2;
	private String arg3;
}
