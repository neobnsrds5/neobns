package com.example.demo.account;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    @Autowired
    private AccountMapper mapper;

    public List<AccountDto> findAll() {
        return mapper.findAll();
    }

    public List<AccountDto> findByPage(Map<String, Object> filters, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.findByPage(filters, offset, size);
    }

    public AccountDto findById(Long id) {
        return mapper.findById(id);
    }

    public void create(AccountDto dto) {
        mapper.insert(dto);
    }

    public void update(Long id, AccountDto dto) {
        mapper.update(dto);
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
