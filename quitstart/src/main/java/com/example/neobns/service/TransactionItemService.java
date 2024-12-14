package com.example.neobns.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.example.neobns.entity.TransactionItem;
import com.example.neobns.repository.TransactionItemRepository;

import propagation.SpiderTransaction;

@Service
public class TransactionItemService {
	
	@Autowired
	private TransactionItemRepository itemRepository;
	
	@Autowired
	private TransactionLoggingService loggingService;
	
//	@SpiderTransaction (propagation = Propagation.REQUIRED, timeout = 5)
	public void persistAnItem() {		
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		System.out.println("persistAnItem 성공?");
		itemRepository.save(item);
		System.out.println("persistAnItem 성공!");
		loggingService.logAMessage( "adding item with name " + item.getName());		
	}

	@SpiderTransaction (propagation = Propagation.REQUIRED, timeout = 1)
	public void persistAnItemFail() {
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		System.out.println("persistAnItemFail 성공?");
		itemRepository.save(item);
		System.out.println("persistAnItemFail 성공!");
		loggingService.logAMessage( "adding item with name " + item.getName());	
		
	}
	
}
