package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
	
	private final RabbitProp prop;
	
	@Bean
	public Queue queue() {
		return new Queue(prop.getQueue(), true);
	}
	
	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(prop.getExchange());
	}
	
	@Bean
	public Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(prop.getRoutingKey());
	}
}
