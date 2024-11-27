package com.example.neobns.service;

import com.example.neobns.dto.AccountDTO;
import com.example.neobns.dto.ItemDto;
import com.example.neobns.mapper.QuickMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public ItemDto getItemById(String id){
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
