package com.example.neobns.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
	private long id;
	private String accountNumber;
	private String name;
	private long money;
}
