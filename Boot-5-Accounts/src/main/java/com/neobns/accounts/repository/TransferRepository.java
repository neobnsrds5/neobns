package com.neobns.accounts.repository;

import com.neobns.accounts.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // 잘못된 SQL 구문: 존재하지 않는 테이블을 참조하여 오류를 발생시킴
    @Query(value = "SELECT * FROM invalid_transfers_table WHERE from_account_number = :accountNumber", nativeQuery = true)
    List<Transfer> findTransfersByInvalidQuery(Long accountNumber);
}
