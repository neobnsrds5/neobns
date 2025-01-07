package com.example.neobns.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
private List<String> messagesList = new ArrayList<>();
	
//	@KafkaListener(topics = "logs", groupId = "example-group")
//	public void listen(ConsumerRecord<String, String> record) {
//		String message = record.value().trim();
//		if (messagesList.size() >= 100) {
//			messagesList.clear();
//		}
//		messagesList.add(message);
//	}
	
	public List<String> getMessages(){
		return messagesList;
	}
}
