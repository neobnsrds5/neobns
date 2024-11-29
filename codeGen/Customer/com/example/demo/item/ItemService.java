package com.example.demo.item;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    @Autowired
    private ItemMapper mapper;

    public List<ItemDto> findAll() {
        return mapper.findAll();
    }

    public List<ItemDto> findByPage(Map<String, Object> filters, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.findByPage(filters, offset, size);
    }

    public ItemDto findById(Long id) {
        return mapper.findById(id);
    }

    public void create(ItemDto dto) {
        mapper.insert(dto);
    }

    public void update(Long id, ItemDto dto) {
        mapper.update(dto);
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
