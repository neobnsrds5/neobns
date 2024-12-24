package com.example.neobns.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransferDTO {
	private String fromAccount;
	private String toAccount;
	private Long money;
}
