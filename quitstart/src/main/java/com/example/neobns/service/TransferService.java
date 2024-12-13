package com.example.neobns.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.dto.TransferDTO;

@Service
public class TransferService {
	
	private final RestTemplate restTemplate;
	
//	@Value("${gateway.url}")
	@Value("http://localhost:8090") // transfer로 바로 요청하도록 수정
	private String transferServiceUrl;
		
	public TransferService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public String initiateTransfer(String fromAccount, String toAccount, long money) {
		
		String url = transferServiceUrl + "/transfer/ex";
			
		TransferDTO dto = TransferDTO.builder()
		.fromAccount(fromAccount)
		.toAccount(toAccount)
		.money(money)
		.build();
		
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, dto, String.class);
			return response.getBody();
		}catch(HttpClientErrorException e){
			return "Transfer failed: " + e.getResponseBodyAsString();
		}
		
	}
}
