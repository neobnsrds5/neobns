package com.example.demo.dto;


import lombok.Data;

@Data
public class AccountDTO {
	private long id;
	private String accountNumber;
	private String name;
	private long money;
}
