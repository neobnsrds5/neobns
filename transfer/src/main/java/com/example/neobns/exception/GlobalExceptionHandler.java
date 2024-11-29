package com.example.neobns.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ch.qos.logback.classic.spi.ILoggingEvent;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred - Class: {}, Message: {}, Status : {}", 
                e.getClass().getName(), e.getMessage(), ResponseEntity.status(HttpStatus.BAD_REQUEST).toString(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException occurred - Class: {}, Message: {}, Status : {}", 
                e.getClass().getName(), e.getMessage(), ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).toString(), e);
        System.out.println("transfer-e2.getMessage : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred transfer");
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Exception occurred - Class: {}, Message: {}, Status : {}", 
                e.getClass().getName(), e.getMessage(), ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).toString(), e);
        System.out.println("transfer-e3.getMessage : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred transfer");
    }
}

