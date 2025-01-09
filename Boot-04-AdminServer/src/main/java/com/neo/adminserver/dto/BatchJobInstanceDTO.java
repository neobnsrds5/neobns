package com.neo.adminserver.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BatchJobInstanceDTO {

	private int instanceId;	
	private int version;
	private String jobName;
	private String jobKey;
	
	private BatchJobExecutionDTO exec;
	
	private List<BatchJobExecutionParamsDTO> execParams;
	
}
