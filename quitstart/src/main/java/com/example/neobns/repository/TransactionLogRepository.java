package com.example.neobns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.neobns.entity.TransactionLog;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Integer> {
	
}
