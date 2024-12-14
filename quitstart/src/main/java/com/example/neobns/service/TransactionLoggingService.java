package com.example.neobns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.neobns.entity.TransactionLog;
import com.example.neobns.repository.TransactionLogRepository;

@Service
public class TransactionLoggingService {
	
	@Autowired
	private TransactionLogRepository logRepository;
	
	@Transactional( propagation = Propagation.SUPPORTS , timeout = 2)
	public void logAMessage(String logMessage) {		
		TransactionLog log = new TransactionLog(logMessage); 
		logRepository.save(log);
		System.out.println("실행");
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //for REQUIRED
		/*
		if(new Random().nextBoolean()) {
			throw new RuntimeException("DummyException: this should cause rollback of both INSERTs (Item and Log)!");
		}
		*/
	}
	
}
