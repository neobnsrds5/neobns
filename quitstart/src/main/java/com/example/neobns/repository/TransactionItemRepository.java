package com.example.neobns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.neobns.entity.TransactionItem;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Integer> {	
	
}
