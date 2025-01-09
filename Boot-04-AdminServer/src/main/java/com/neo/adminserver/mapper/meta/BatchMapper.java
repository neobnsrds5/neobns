package com.neo.adminserver.mapper.meta;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.neo.adminserver.dto.BatchJobExecutionDTO;
import com.neo.adminserver.dto.BatchJobInstanceDTO;
import com.neo.adminserver.dto.BatchStepExecutionDTO;

@Mapper
public interface BatchMapper {
	public int listCount(HashMap<String, Object> map);

	public List<BatchJobInstanceDTO> list(HashMap<String, Object> map);

	public BatchJobInstanceDTO selectJobDetail(int id);

	public BatchJobExecutionDTO selectStepDetail(int id);

	public List<BatchStepExecutionDTO> listStepDetail(int id);
}
