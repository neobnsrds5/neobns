package com.neobns.admin.batch.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.neobns.admin.batch.dto.BatchJobExecutionDTO;
import com.neobns.admin.batch.dto.BatchJobInstanceDTO;
import com.neobns.admin.batch.dto.BatchStepExecutionDTO;

@Mapper
public interface BatchMapper {
	public int countJobs(HashMap<String, Object> map);

	public List<BatchJobInstanceDTO> findJobs(HashMap<String, Object> map);

	public BatchJobInstanceDTO findJobById(int id);

	public BatchJobExecutionDTO findStepById(int id);

	public List<BatchStepExecutionDTO> findStepsByJobId(int id);
}
