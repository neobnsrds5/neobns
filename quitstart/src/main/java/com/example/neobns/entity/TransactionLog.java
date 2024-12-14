package com.example.neobns.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_log")
public class TransactionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
    private String message;
    
    private LocalDate date;

    public TransactionLog() {}
    public TransactionLog(String message) {
        this.message = message;
        this.date = LocalDate.now();
    }

	@Override
	public String toString() {
		return "Log [id=" + id + ", message=" + message + ", date=" + date + "]";
	}    
    
}
