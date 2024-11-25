package com.demo.customer;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMapper {

    List<CustomerDto> findAll(Map<String, Object> filters);

    List<CustomerDto> findByPage(Map<String, Object> filters, int offset, int limit);

    CustomerDto findById(Long id);

    void insert(CustomerDto dto);

    void update(CustomerDto dto);

    void deleteById(Long id);
}
