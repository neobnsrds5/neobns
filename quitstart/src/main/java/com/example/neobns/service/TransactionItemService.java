package com.example.neobns.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.example.neobns.entity.TransactionItem;
import com.example.neobns.repository.TransactionItemRepository;

import jakarta.transaction.Transactional;
import propagation.SpiderTransaction;

@Service
public class TransactionItemService {

	@Autowired
	private TransactionItemRepository itemRepository;

	@Autowired
	private TransactionLoggingService loggingService;

	@Transactional(rollbackOn = RuntimeException.class)
	public void persistAnItem() {
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		System.out.println("persistAnItem 성공?");
		TransactionItem retuenItem = itemRepository.save(item);
		System.out.println("반환된 아이템1 : " + retuenItem.toString());
		itemRepository.save(item);

		try {

			throw new RuntimeException("강제 롤백 처리");

		} catch (Exception e) {
			boolean isRollBacked = TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();

			System.out.println("1 is rollbacked? " + isRollBacked);
			
			throw e;
		}

	}

	@SpiderTransaction(propagation = Propagation.REQUIRED, timeout = 1)
	public void persistAnItemFail() {
		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 30);
		System.out.println("persistAnItemFail 성공?");
		TransactionItem retuenItem = itemRepository.save(item);
		System.out.println("반환된 아이템2 : " + retuenItem.toString());

		loggingService.logAMessageFail("adding item with name " + item.getName());
		

		try {
			
			throw new RuntimeException("강제 롤백 처리");
			
		} catch (Exception e) {
			boolean isRollBacked = TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();

			System.out.println("2 is rollbacked? " + isRollBacked);
			
			throw e;
		}

	}

}
