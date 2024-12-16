package com.spider.demo.endpoint;

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import com.spider.demo.entity.TransactionItem;
import com.spider.demo.repository.TransactionItemRepository;

@Component
@Endpoint(id = "transaction-item")
public class TransactionItemEndpoint {

	private final TransactionItemRepository transactionItemRepository;

	public TransactionItemEndpoint(TransactionItemRepository transactionItemRepository) {
		super();
		this.transactionItemRepository = transactionItemRepository;
	}

	@ReadOperation
	public List<TransactionItem> getAllItems() {
		return transactionItemRepository.findAll();
	}

}
