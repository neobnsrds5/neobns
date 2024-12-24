package com.example.neobns.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.service.KafkaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {
	private final KafkaService service;
	
	@GetMapping("/print")
	public List<String> getMessages(){
		List<String> result = service.getMessages();
		return result;
	}
}
