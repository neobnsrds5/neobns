package com.neobns.accounts.controller;

import java.net.BindException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neobns.accounts.dto.ItemDto;
import com.neobns.accounts.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
	private final TestService testService;
	private final Logger log = LoggerFactory.getLogger(TestController.class);
	@GetMapping("/item")
	public ItemDto getItem(@RequestParam("id") String id) {
		log.info(id);
		ItemDto itemDto = testService.getItemById(id);
		
		return itemDto;
	}
	
	@GetMapping("/error/item")
	public ItemDto getItemFromWrongTable(@RequestParam("id") String id) {
		return testService.getItemFromWrongTable(id);
		
	}
	@GetMapping("/error")
	public void getError() throws BindException {
		throw new BindException();
	}
}
