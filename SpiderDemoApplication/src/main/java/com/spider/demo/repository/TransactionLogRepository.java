package com.spider.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spider.demo.entity.TransactionLog;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Integer> {
	
}
	