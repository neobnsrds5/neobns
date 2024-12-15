package com.spider.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spider.demo.entity.TransactionItem;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Integer> {	
	
}
