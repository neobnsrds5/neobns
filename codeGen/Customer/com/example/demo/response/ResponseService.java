package com.example.demo.response;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class ResponseService {

    @Autowired
    private ResponseMapper mapper;

    public List<ResponseDto> findAll() {
        return mapper.findAll();
    }

    public List<ResponseDto> findByPage(Map<String, Object> filters, int page, int size) {
        int offset = (page - 1) * size;
        return mapper.findByPage(filters, offset, size);
    }

    public ResponseDto findById(Long id) {
        return mapper.findById(id);
    }

    public void create(ResponseDto dto) {
        mapper.insert(dto);
    }

    public void update(Long id, ResponseDto dto) {
        mapper.update(dto);
    }

    public void delete(Long id) {
        mapper.deleteById(id);
    }
}
