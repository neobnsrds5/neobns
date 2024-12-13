package com.example.neobns.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.neobns.entity.ErrorDetail;

import java.util.List;

@Repository
public interface ErrorDetailRepository extends CrudRepository<ErrorDetail,String> {

    @Query( value = " SELECT * from error_details error WHERE  (TO_SECONDS(sysdate())-TO_SECONDS(error.date)) < " +
            ":duration", nativeQuery = true)
    List<ErrorDetail> getRecord(Long duration);
}