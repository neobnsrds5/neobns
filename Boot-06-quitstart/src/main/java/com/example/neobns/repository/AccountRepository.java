package com.example.neobns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.neobns.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
