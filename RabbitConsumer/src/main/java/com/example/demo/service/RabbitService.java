package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {
	
	private List<String> messageList = new ArrayList<>();
	
    @RabbitListener(queues = "example-queue")
    public void receive(String message) {
    	messageList.add(message);
        System.out.println("Received: " + message);
    }
    
    public List<String> getMessages(){
    	
    	return messageList;
    }
}
