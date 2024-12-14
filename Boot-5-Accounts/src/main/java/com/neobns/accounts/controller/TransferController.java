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
	public ResponseEntity<String> makeSqlError(@PathVariable Long accountNumber){
		System.out.println("에러쿼리컨트롤러 호출");
		 try {
	            List<Transfer> transfers = transferService.fetchTransfersByInvalidQuery(accountNumber);
	            return ResponseEntity.ok("Transfers fetched successfully.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error occurred: " + e.getMessage());
	        }
	}
}
