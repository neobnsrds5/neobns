package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RabbitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RabbitController {
	
	private final RabbitService service;
	
	@GetMapping("/rabbit")
	public List<String> getMessages(){
		return service.getMessages();
	}
}
