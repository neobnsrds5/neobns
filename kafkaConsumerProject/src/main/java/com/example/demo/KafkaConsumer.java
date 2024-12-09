package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	@KafkaListener(topics = "logs", groupId = "example-group")
	public void listen(ConsumerRecord<String, String> record) {
		System.out.println(record.value());
	}
}