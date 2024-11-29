package com.example.demo.account;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {

    List<AccountDto> findAll();

    List<AccountDto> findByPage(Map<String, Object> filters, int offset, int limit);

    AccountDto findById(Long id);

    void insert(AccountDto dto);

    void update(AccountDto dto);

    void deleteById(Long id);
}
