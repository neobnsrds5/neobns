package com.spider.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spider.demo.service.TransactionItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PropagationController {

	private final TransactionItemService itemService;

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

}
