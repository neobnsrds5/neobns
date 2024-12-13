package com.example.neobns.mapper;

import com.example.neobns.dto.ItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuickMapper {
    ItemDto findById(String id);
    List<ItemDto> findAll();
}
