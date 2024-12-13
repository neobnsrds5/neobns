package com.example.neobns.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.dto.CustomerDto;
import com.example.neobns.service.RestCallAccountsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestCallController {
	
	private final RestCallAccountsService restCallAccountsService;

	
	@PostMapping("/accounts")
	public ResponseEntity<String> toAccounts(@RequestBody CustomerDto dto){
		log.info("ToAccount 호출");
		
		log.info("Received CustomerDto: {}", dto);
		
		String result = restCallAccountsService.initiateRestCall(dto);
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/transfers/{accountNumber}")
	public ResponseEntity<String> toTransfers(@PathVariable Long accountNumber){
		log.info("ToTransfer 호출");
		
		String result = restCallAccountsService.initiateESql(accountNumber);
		return ResponseEntity.ok(result);
	}
}
