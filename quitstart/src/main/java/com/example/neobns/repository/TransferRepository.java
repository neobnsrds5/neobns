package com.example.neobns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.neobns.entity.TransferEntity;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> {

}
