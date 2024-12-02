package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testForFileSystemVault {
	
	@Value("${test.message.name}")
	private String name;
	
	@GetMapping("/api/test")
	public String getPropertiesTest() {
		return name;
	}
	

}
