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
@RequiredArgsConstructor
@Slf4j
public class TransferController {
	private final TransferService transferService;
	
	@PostMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestBody TransferDTO dto){
		
		log.info("이체 시작하기 위해 메인 서비스에서 호출 from : {}, to : {}, money : {}", dto.getFromAccount(), dto.getToAccount(), dto.getMoney());
		String result = transferService.initiateTransfer(dto.getFromAccount(), dto.getToAccount(), dto.getMoney());
		log.info("이체 서비스를 통해 이체 완료");
		return ResponseEntity.ok(result);
	}
}
