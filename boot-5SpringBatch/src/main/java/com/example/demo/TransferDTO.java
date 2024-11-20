package com.example.demo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TransferDTO {
	private String fromAccount;
	private String toAccount;
	private Long money;
	
	@Builder
	public TransferDTO(String fromAccount, String toAccount, Long money) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.money = money;
	}
	
}
