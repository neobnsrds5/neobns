package com.example.neobns.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
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
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(getId(), account.getId());
    }
 
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
	

}
