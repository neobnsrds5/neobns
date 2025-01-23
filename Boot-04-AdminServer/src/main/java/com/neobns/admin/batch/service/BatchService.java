package com.neobns.admin.batch.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.neobns.admin.batch.dto.BatchJobExecutionDTO;
import com.neobns.admin.batch.dto.BatchJobInstanceDTO;
import com.neobns.admin.batch.dto.BatchStepExecutionDTO;
import com.neobns.admin.batch.mapper.BatchMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchService {
	
	private final BatchMapper batchMapper;

	public int countJobs(HashMap<String, Object> map) {
		return batchMapper.countJobs(map);
	}

	public List<BatchJobInstanceDTO> findJobs(HashMap<String, Object> map) {
		return batchMapper.findJobs(map);
	}

	public BatchJobInstanceDTO findJobById(int id) {
		return batchMapper.findJobById(id);
	}

	public BatchJobExecutionDTO findStepById(int id) {
		return batchMapper.findStepById(id);
	}

	public List<BatchStepExecutionDTO> findStepsByJobId(int id) {
		return batchMapper.findStepsByJobId(id);
	}
}
