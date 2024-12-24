package com.example.neobns.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.service.MessageProducer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ProducerController {
	
	private final MessageProducer messageProducer;
	
	@PostMapping("/send")
	public String sendMessage(@RequestBody String message) {
		//TODO: process POST request
		messageProducer.sendMessage(message);
		return "Message sent: " + message;
	}
	
	
}
