package com.neobns.admin.batch.dto;

import java.util.Date;
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

	// 검색을 위한 멤버변수
	private Date startDate;
	private Date endDate;
	private String status;
}
