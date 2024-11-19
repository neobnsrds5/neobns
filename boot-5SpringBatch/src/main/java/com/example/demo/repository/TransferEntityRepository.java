package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TransferEntity;

public interface TransferEntityRepository extends JpaRepository<TransferEntity, Long> {

}
