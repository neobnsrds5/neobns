package com.example.neobns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.spi.LoggingEvent;

import java.util.List;

@Repository
public interface LoggingEventRepository extends JpaRepository<LoggingEvent, Long> {

    // timestmp, logger_name, execute_result를 가져오는 쿼리
    @Query("SELECT timestmp, logger_name, execute_result FROM logging_event ")
    List<Object[]> findAllLoggingEvents();
}
