package com.example.demo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProducerService {
	
	private final KafkaTemplate<String, String> template;
	
	public void sendMessage(String topic, String message) {
		template.send(topic, message);
	}
}
