package com.example.neobns.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.neobns.dto.CustomerDto;
import com.example.neobns.entity.Customer;
import com.example.neobns.exception.CustomerAlreadyExistsException;
import com.example.neobns.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class RestCallAccountsService {
	
	private final RestTemplate restTemplate;
	private final CustomerRepository customerRepository;
	
	@Value("http://localhost:19999")
	private String accountsUrl;
	
	public String initiateRestCall(CustomerDto customer) {
		String url = accountsUrl + "/api/create";
		
		log.info("Rest서비스를 통해 restCall 진행 : {}", url);
		try{
			
			Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customer.getMobileNumber());
			if(optionalCustomer.isPresent()) {
				throw new CustomerAlreadyExistsException("이미 가입된 고객의 핸드폰 번호입니다. 핸드폰 번호 : " + customer.getMobileNumber());
			}
			
			ResponseEntity<String> response = restTemplate.postForEntity(url, customer, String.class);
			return response.getBody();
		}catch (HttpClientErrorException e) {
			return "RestCall Failed : " + e.getResponseBodyAsString();
		}
	}
	
	public String initiateESql(Long accountNumber) {
		
		String url = accountsUrl + "/transfers/ex/";
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url+accountNumber, String.class);
			return response.getBody();
		}catch (HttpClientErrorException | org.springframework.web.server.ResponseStatusException e) {
            // 오류가 발생하면 오류 메시지 출력
            return "Error occurred while calling the external API: " + e.getMessage();
        }
		
	}
}
