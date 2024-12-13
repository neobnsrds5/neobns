package com.example.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaConsumerController {
	private final KafkaConsumer service;
	
	@GetMapping("/print")
	public List<String> getMessages(){
		List<String> result = service.getMessages();
		return result;
	}
}
