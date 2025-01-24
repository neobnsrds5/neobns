package com.neobns.admin.batch.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.neobns.admin.batch.dto.BatchJobExecutionDTO;
import com.neobns.admin.batch.dto.BatchJobInstanceDTO;
import com.neobns.admin.batch.dto.BatchStepExecutionDTO;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchMapper {
	int countJobs(BatchJobInstanceDTO paramDto);

	List<BatchJobInstanceDTO> findJobs(@Param("paramDto") BatchJobInstanceDTO paramDto, @Param("limit") int limit, @Param("offset") int offset);

	BatchJobInstanceDTO findJobById(int id);

	BatchJobExecutionDTO findStepById(int id);

	List<BatchStepExecutionDTO> findStepsByJobId(int id);
}
