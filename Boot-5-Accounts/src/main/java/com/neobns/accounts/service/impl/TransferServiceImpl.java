package com.neobns.accounts.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.neobns.accounts.dto.CustomerDto;
import com.neobns.accounts.entity.Transfer;
import com.neobns.accounts.repository.TransferRepository;
import com.neobns.accounts.service.ITransferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements ITransferService{

	
	private final TransferRepository transferRepository;
	private final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
	
	 public List<Transfer> fetchTransfersByInvalidQuery(Long accountNumber) {
	        try {
	            return transferRepository.findTransfersByInvalidQuery(accountNumber);
	        } catch (DataAccessException dae) {
	            logger.error("SQL Syntax Error during query execution: {}", dae.getMessage(), dae);
	            throw dae;
	        }
	    }

}
