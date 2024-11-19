package com.example.demo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyDTO {
	private String fromAccount;
	private String toAccount;
	private Long money;
	
	@Builder
	public MyDTO(String fromAccount, String toAccount, Long money) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.money = money;
	}
	
}
