package com.example.demo.transfer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class TransferService {

    @Autowired
    private TransferMapper mapper;

    public List<TransferDto> findAll() {
        return mapper.findAll();
    }

    public List<TransferDto> findByPage(Map<String, Object> filters, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.findByPage(filters, offset, size);
    }

    public TransferDto findById(Long id) {
        return mapper.findById(id);
    }

    public void create(TransferDto dto) {
        mapper.insert(dto);
    }

    public void update(Long id, TransferDto dto) {
        mapper.update(dto);
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
