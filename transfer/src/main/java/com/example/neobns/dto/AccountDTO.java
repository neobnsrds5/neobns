package com.example.neobns.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class AccountDTO {
	private long id;
	private String accountNumber;
	private String name;
	private long money;
	
	@Override
	public String toString() {
		return "AccountDTO [id=" + id + ", accountNumber=" + accountNumber + ", name=" + name + ", money=" + money
				+ "]";
	}
	
	
}
