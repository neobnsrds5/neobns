package com.example.demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
	@Bean
	public Queue queue() {
		return new Queue(MessagingConstants.QUEUE_NAME, true);
	}
	
	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(MessagingConstants.EXCHANGE_NAME);
	}
	
	@Bean
	public Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(MessagingConstants.ROUTING_KEY);
	}
}
