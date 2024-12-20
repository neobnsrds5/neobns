package com.spider.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spider.demo.entity.TransactionLog;
import com.spider.demo.repository.TransactionLogRepository;

import dev.failsafe.Timeout;

@Service
public class TransactionLoggingService {

	@Autowired
	private TransactionLogRepository logRepository;

	// 항상 로그 메세지 데이터 저장
	public void logAMessageSuccess(String logMessage) {
		TransactionLog log = new TransactionLog(logMessage);
		logRepository.save(log);
	}

	// 타임아웃에 의한 롤백으로 로그 메세지 데이터 저장 안됨 
	@Transactional(propagation = Propagation.SUPPORTS, timeout = 1)
	public void logAMessageFail(String logMessage) {
		
		try {
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		TransactionLog log = new TransactionLog(logMessage);
		logRepository.save(log);
		
		// for REQUIRED
		/*
		 * if(new Random().nextBoolean()) { throw new
		 * RuntimeException("DummyException: this should cause rollback of both INSERTs (Item and Log)!"
		 * ); }
		 */
	}

	public List<TransactionLog> getAllLogs() {
		// TODO Auto-generated method stub
		return logRepository.findAll();
	}

}
