package com.spider.demo.endpoint;

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import com.spider.demo.entity.TransactionLog;
import com.spider.demo.repository.TransactionLogRepository;

@Component
@Endpoint(id = "transaction-log")
public class TransactionLogEndpoint {
	private final TransactionLogRepository transactionLogRepository;

	public TransactionLogEndpoint(TransactionLogRepository transactionLogRepository) {
		super();
		this.transactionLogRepository = transactionLogRepository;
	}

	@ReadOperation
	public List<TransactionLog> getAlllogs() {
		return transactionLogRepository.findAll();
	}
}
