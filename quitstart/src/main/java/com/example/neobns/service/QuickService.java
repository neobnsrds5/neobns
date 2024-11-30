package com.example.neobns.service;

import com.example.neobns.dto.AccountDTO;
import com.example.neobns.dto.ItemDto;
import com.example.neobns.mapper.QuickMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuickService {

    private final QuickMapper mapper;
    
    public boolean registerItem(ItemDto itemDto){
        log.info("Service....");
        mapper.addItem(itemDto);
        return true;
    }

    @Cacheable(value = "item:id", key="#id")
    public ItemDto getItemById(String id){
    	System.out.println("getItemById : " + id + " 실행됨~");
        ItemDto itemDto = mapper.findById(id);
        return itemDto;
    }

    public List<ItemDto> getAll() {
        List<ItemDto> results = mapper.findAll();
        return results;
    }

	public void addAccount(AccountDTO dto) {
		// TODO Auto-generated method stub
		mapper.addAccount(dto);
	}
}
