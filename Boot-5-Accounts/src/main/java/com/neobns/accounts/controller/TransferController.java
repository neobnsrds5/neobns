package com.neobns.accounts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neobns.accounts.entity.Transfer;
import com.neobns.accounts.service.ITransferService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

	private final ITransferService transferService;
	
	@GetMapping("/ex/{accountNumber}")
	public ResponseEntity<String> makeSqlError(@PathVariable Long accountNumber) {
	    System.out.println("에러쿼리컨트롤러 호출");
	    // 예외를 잡지 않고, 서비스에서 발생한 예외를 그대로 던져서 GlobalExceptionHandler로 위임
	    transferService.fetchTransfersByInvalidQuery(accountNumber);
	    
	    // 정상적인 흐름에서는 이 코드가 실행되지 않도록 함
	    return ResponseEntity.ok("Transfers fetched successfully.");
	}



}
