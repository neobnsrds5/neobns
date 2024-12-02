package com.example.demo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
	@RabbitListener(queues = MessagingConstants.QUEUE_NAME)
	public void receiveMessage(String message) {
		System.out.println("Received: " + message);
	}
}
