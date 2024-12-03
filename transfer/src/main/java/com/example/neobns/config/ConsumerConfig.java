package com.example.neobns.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Bean
    public Queue queue(){
        return new Queue("example-queue", true);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange("example-exchange");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("example-routing-key");
    }
}
