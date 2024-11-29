package com.example.demo.transfer;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface TransferMapper {

    List<TransferDto> findAll();

    List<TransferDto> findByPage(Map<String, Object> filters, int offset, int limit);

    TransferDto findById(Long id);

    void insert(TransferDto dto);

    void update(TransferDto dto);

    void deleteById(Long id);
}
