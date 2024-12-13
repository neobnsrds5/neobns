package com.example.neobns.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.neobns.config.MessagingConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageProducer {
	
	private final RabbitTemplate rabbitTemplate;
	
	public void sendMessage(String message) {
		rabbitTemplate.convertAndSend(MessagingConstants.EXCHANGE_NAME, MessagingConstants.ROUTING_KEY, message);
	}
}
