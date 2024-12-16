package com.example.neobns.service;

import org.springframework.stereotype.Service;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.mapper.TransferMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferService {
	
	private final TransferMapper transferMapper;
	
	public void insertTransferDetail(TransferDTO transferDTO) {
		transferMapper.insertTransferDetails(transferDTO);
	}
}
