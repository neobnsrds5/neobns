package com.neo.adminserver.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.BatchJobExecutionDTO;
import com.neo.adminserver.dto.BatchJobInstanceDTO;
import com.neo.adminserver.dto.BatchStepExecutionDTO;
import com.neo.adminserver.mapper.meta.BatchMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {
	
	private final BatchMapper batchMapper;

	public int listCount(HashMap<String, Object> map) {
		return batchMapper.listCount(map);
	}

	public List<BatchJobInstanceDTO> list(HashMap<String, Object> map) {
		return batchMapper.list(map);
	}

	public BatchJobInstanceDTO selectJobDetail(int id) {
		return batchMapper.selectJobDetail(id);
	}

	public BatchJobExecutionDTO selectStepDetail(int id) {
		return batchMapper.selectStepDetail(id);
	}

	public List<BatchStepExecutionDTO> listStepDetail(int id) {
		return batchMapper.listStepDetail(id);
	}
}
