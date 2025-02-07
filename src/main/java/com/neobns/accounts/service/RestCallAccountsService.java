package com.neobns.accounts.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.neobns.accounts.dto.CustomerDto;
import com.neobns.accounts.entity.Customer;
import com.neobns.accounts.exception.CustomerAlreadyExistsException;
import com.neobns.accounts.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestCallAccountsService {
	
	private final RestTemplate restTemplate; 
	private final CustomerRepository customerRepository;

	@Value("http://localhost:8080")
	private String accountsUrl;
	
	public String initiateRestCall(CustomerDto customer) {
		String url = accountsUrl + "/api/create";
		
		try{
			
			Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customer.getMobileNumber());
			if(optionalCustomer.isPresent()) {
				log.info("9090에서 에러 발생");
				throw new CustomerAlreadyExistsException("이미 가입된 고객의 핸드폰 번호입니다. 핸드폰 번호 : " + customer.getMobileNumber());
			}
			
			ResponseEntity<String> response = restTemplate.postForEntity(url, customer, String.class);
			return response.getBody();
		}catch (HttpClientErrorException e) {
			return "RestCall Failed : " + e.getResponseBodyAsString();
		}
	}
	
	public ResponseEntity initiateErr(CustomerDto customer) {
		String url = accountsUrl + "/api/create/err";
		
		try{
//			ResponseEntity<String> response =
			return  restTemplate.postForEntity(url, customer, String.class);
//			return response.getBody();
		}catch (HttpClientErrorException e) {
//			return "RestCall Failed : " + e.getResponseBodyAsString();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("failed to call : " + e.getMessage());
		}
	}
}
