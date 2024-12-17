package com.spider.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spider.demo.entity.TransactionItem;
import com.spider.demo.entity.TransactionLog;
import com.spider.demo.service.TransactionItemService;
import com.spider.demo.service.TransactionLoggingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PropagationController {

	private final TransactionItemService itemService;
	private final TransactionLoggingService loggingService;

	// propagation 시 데이터 입력됨
	@GetMapping("/propagationInsert")
	public void persistItemSucess() {

		itemService.persistAnItem();

	}
	
	// propagation 시 타임아웃으로 데이터 입력 안됨
	@GetMapping("/propagationRollback")
	public void persistItemFail() {
		
		itemService.persistAnItemFail();
		
	}
	
	// propagation 시 타임아웃으로 데이터 입력 안됨
	@GetMapping("/getAllItems")
	public List<TransactionItem> getAllItems() {
		
		List<TransactionItem> list = itemService.getAllItems();
		
		return list;
		
	}
	
	// propagation 시 타임아웃으로 데이터 입력 안됨
	@GetMapping("/getAllLogs")
	public List<TransactionLog> getAllLogs() {
		
		List<TransactionLog> list  = loggingService.getAllLogs();
		
		return list;
	}

}
