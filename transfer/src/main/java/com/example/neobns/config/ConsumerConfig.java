package com.example.neobns.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfig {
	
	private RabbitProp prop;

    @Bean
    public Queue queue(){
        return new Queue(prop.getQueue(), true);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(prop.getExchange());
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(prop.getRoutingKey());
    }
}
