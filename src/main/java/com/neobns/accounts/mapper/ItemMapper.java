package com.neobns.accounts.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.neobns.accounts.dto.ItemDto;

@Mapper
public interface ItemMapper {
	ItemDto findById(String id);
	ItemDto  findFromWrongTable(String id);
}
