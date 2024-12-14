package com.neobns.accounts.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.neobns.accounts.dto.AccountsDto;
import com.neobns.accounts.entity.Accounts;

import lombok.RequiredArgsConstructor;

@Mapper
@RequiredArgsConstructor
public class AccountsMapper {
	
    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto) {
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        return accounts;
    }
}
