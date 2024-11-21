package com.example.neobns.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private String accountNumber;
	
	@NotNull
	private Long money;
	
	@Builder
	public Account(@NotNull String name, @NotNull String accountNumber) {
		super();
		this.name = name;
		this.accountNumber = accountNumber;
	}
	
	@Builder
	public Account(@NotNull String name, @NotNull String accountNumber, @NotNull Long money) {
		super();
		this.name = name;
		this.accountNumber = accountNumber;
		this.money = money;
	}
	
	
	
}
