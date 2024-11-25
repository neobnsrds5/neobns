package com.example.neobns.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.service.TransferService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TransferController {
	
	private final TransferService transferService;

	@PostMapping("/ex")
	public ResponseEntity<String> transfer(@RequestBody TransferDTO request){
		log.info("이체 서비스에서 이체 기능 호출 ");
		if(request.getMoney()>0) {
			transferService.insertTransferDetail(request);
			return ResponseEntity.ok("Transfer succesful: " + request.getMoney() + " to " + request.getToAccount());
		}else {
			return ResponseEntity.badRequest().body("Invalid transfer amount");
		}
		
		
	}
}
