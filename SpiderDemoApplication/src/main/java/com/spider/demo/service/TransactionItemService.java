package com.spider.demo.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spider.demo.entity.TransactionItem;
import com.spider.demo.propagation.SpiderTransaction;
import com.spider.demo.repository.TransactionItemRepository;

@Service
public class TransactionItemService {

	@Autowired
	private TransactionItemRepository itemRepository;

	@Autowired
	private TransactionLoggingService loggingService;

	public void persistAnItem() {
		
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		itemRepository.save(item);
		loggingService.logAMessageSuccess( "adding item with name " + item.getName());		

	}

	@SpiderTransaction(propagation = Propagation.REQUIRED, timeout = 1)
	public void persistAnItemFail() {
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		TransactionItem item = new TransactionItem("Item2", LocalDate.of(2022, 5, 1), 30);
		itemRepository.save(item);
		loggingService.logAMessageFail( "adding item with name " + item.getName());		
		
		
	}

}
