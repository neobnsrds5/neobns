package com.example.neobns.service;

import com.example.neobns.dto.ItemDto;
import com.example.neobns.mapper.TransferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {

    private final TransferMapper mapper;

    public boolean registerItem(ItemDto itemDto){
        log.info("Service....");
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
}
