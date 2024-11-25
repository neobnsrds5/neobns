package com.demo.customer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    private CustomerMapper mapper;

    @Transactional(readOnly = true)
    public List<CustomerDto> findAll(Map<String, Object> filters) {
        return mapper.findAll(filters);
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> findByPage(Map<String, Object> filters, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.findByPage(filters, offset, size);
    }

    @Transactional(readOnly = true)
    public CustomerDto findById(Long id) {
        return mapper.findById(id);
    }

    @Transactional
    public void create(CustomerDto dto) {
        mapper.insert(dto);
    }

    @Transactional
    public void update(Long id, CustomerDto dto) {
        mapper.update(dto);
    }

    @Transactional
    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
