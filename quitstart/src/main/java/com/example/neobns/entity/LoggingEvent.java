package com.example.neobns.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LoggingEvent {

    @Id
    private Long eventId; // 기본 키 (예시)

    @Column(name = "timestmp")
    private java.sql.Timestamp timestmp;

    @Column(name = "logger_name")
    private String loggerName;

    @Column(name = "execute_result")
    private String executeResult;
}
