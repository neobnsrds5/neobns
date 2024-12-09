package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kafka")
public class ProducerController {
	
	private final ProducerService service;
	
	@PostMapping("/send")
	public String sendMessage(@RequestBody String message) {
		service.sendMessage("quickstart-events", message);
		return "OK";
	}
}
