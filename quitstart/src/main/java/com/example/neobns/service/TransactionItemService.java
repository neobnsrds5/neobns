package com.example.neobns.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.neobns.entity.TransactionItem;
import com.example.neobns.repository.TransactionItemRepository;

import propagation.SpiderTransaction;

@Service
public class TransactionItemService {

	@Autowired
	private TransactionItemRepository itemRepository;

	@Autowired
	private TransactionLoggingService loggingService;

	@Transactional
	public void persistAnItem() {
		
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		itemRepository.save(item);
		loggingService.logAMessageSuccess( "adding item with name " + item.getName());		

		throw new RuntimeException("강제 롤백 처리");

	}

	@SpiderTransaction(propagation = Propagation.REQUIRED, timeout = 1)
	public void persistAnItemFail() {
		TransactionItem item = new TransactionItem("Item2", LocalDate.of(2022, 5, 1), 29);
		itemRepository.save(item);
		loggingService.logAMessageFail( "adding item with name " + item.getName());		

		throw new RuntimeException("강제 롤백 처리");

	}

}
