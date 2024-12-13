package com.example.neobns.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.neobns.dto.ErrorLogDTO;
import com.example.neobns.mapper.ErrorMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErrorService {
	
	private final ErrorMapper mapper;
	
	public ErrorLogDTO getError() {
		List<ErrorLogDTO> result = mapper.getRecord(10L);
		System.out.println(result.get(0).getEvent_id());
		return result.get(0);
	}
}
