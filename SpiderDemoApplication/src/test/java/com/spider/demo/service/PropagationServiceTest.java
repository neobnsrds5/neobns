package com.spider.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.spider.demo.entity.TransactionItem;
import com.spider.demo.entity.TransactionLog;
import com.spider.demo.repository.TransactionItemRepository;
import com.spider.demo.service.TransactionItemService;
import com.spider.demo.service.TransactionLoggingService;

import dev.failsafe.internal.util.Assert;

@SpringBootTest
public class PropagationServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(PropagationServiceTest.class);

	@Autowired
	private TransactionItemService transactionItemService;

	@Autowired
	private TransactionItemRepository itemRepository;

	@Autowired
	private TransactionLoggingService loggingService;

	@Test
	void propagationInsert() throws Exception {

		TransactionItem item = new TransactionItem("Item1", LocalDate.of(2022, 5, 1), 29);
		String logMessage = "adding item with name " + item.getName();

		TransactionLog log = new TransactionLog(logMessage);

		transactionItemService.persistAnItem();

		List<TransactionItem> result = transactionItemService.getAllItems();
		List<TransactionLog> result2 = loggingService.getAllLogs();
		Assertions.assertThat(result).contains(item);
		Assertions.assertThat(result2).contains(log);

	}

	@Test
	public void persistAnItemFail() throws Exception {



	}

}
