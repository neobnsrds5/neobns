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

	public int countJobs(BatchJobInstanceDTO paramDto) {
		return batchMapper.countJobs(paramDto);
	}

	public List<BatchJobInstanceDTO> findJobs(BatchJobInstanceDTO paramDto, int page, int size) {
		int offset = (page - 1) * size;
		return batchMapper.findJobs(paramDto, size, offset);
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
