package com.neobns.accounts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.neobns.accounts.dto.CustomerDto;
import com.neobns.accounts.service.RestCallAccountsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RestCallController {
	
	private final RestCallAccountsService restCallAccountsService;
	
	@PostMapping("/accounts")
	public ResponseEntity<String> toAccounts(@RequestBody CustomerDto dto){
		
		String result = restCallAccountsService.initiateRestCall(dto);
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/accounts/err")
	public ResponseEntity<String> toAccountsErr(@RequestBody CustomerDto dto){
		return restCallAccountsService.initiateErr(dto);
	}
}
