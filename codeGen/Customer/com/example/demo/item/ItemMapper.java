package com.example.demo.item;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ItemMapper {

    List<ItemDto> findAll();

    List<ItemDto> findByPage(Map<String, Object> filters, int offset, int limit);

    ItemDto findById(Long id);

    void insert(ItemDto dto);

    void update(ItemDto dto);

    void deleteById(Long id);
}
