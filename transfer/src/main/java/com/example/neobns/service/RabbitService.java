package com.example.neobns.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @RabbitListener(queues = "example-queue")
    public void receive(String message) {
        System.out.println("Received: " + message);
    }
}
