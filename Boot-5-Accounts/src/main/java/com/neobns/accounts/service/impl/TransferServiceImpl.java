package com.neobns.accounts.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.stereotype.Service;

import com.neobns.accounts.dto.CustomerDto;
import com.neobns.accounts.entity.Transfer;
import com.neobns.accounts.repository.TransferRepository;
import com.neobns.accounts.service.ITransferService;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements ITransferService {

	private final TransferRepository transferRepository;
	private final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

	public List<Transfer> fetchTransfersByInvalidQuery(Long accountNumber) {
	    try {
	        logger.info("Executing SQL query for accountNumber: {}", accountNumber);
	        List<Transfer> result = transferRepository.findTransfersByInvalidQuery(accountNumber);
	        
	        if (result == null || result.isEmpty()) {
	            logger.warn("No transfers found for accountNumber: {}", accountNumber);
	            throw new RuntimeException("Database Query Error");
	        }

	        log.info("잘못된 쿼리 수행 결과 : {}", result);
	        return result;
	    } catch (PersistenceException pe) {
	        logger.error("Persistence error during query execution: {}", pe.getMessage(), pe);
	        throw new RuntimeException("Persistence error: " + pe.getMessage(), pe);
	    } catch (DataAccessException dae) {
	        logger.error("Data access error during query execution: {}", dae.getMessage(), dae);
	        throw new RuntimeException("Database query error: " + dae.getMessage(), dae);
	    } catch (Exception e) {
	        logger.error("Unexpected error: {}", e.getMessage(), e);
	        throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
	    }
	}


}
