package com.example.neobns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.neobns.dto.AccountDTO;

@Mapper
public interface TargetMapper {

	@Select("SELECT * FROM db3.Account")
	List<AccountDTO> findAllAccounts();

}
