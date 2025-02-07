package com.neobns.accounts.service;

import org.springframework.stereotype.Service;

import com.neobns.accounts.dto.ItemDto;
import com.neobns.accounts.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
	private final ItemMapper itemMapper;
	
	public ItemDto getItemById(String id) {
		return itemMapper.findById(id);
	}
	
	public ItemDto getItemFromWrongTable(String id) {
		return itemMapper.findFromWrongTable(id);
				
	}
}
