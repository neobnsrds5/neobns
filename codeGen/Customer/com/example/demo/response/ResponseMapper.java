package com.example.demo.response;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ResponseMapper {

    List<ResponseDto> findAll();

    List<ResponseDto> findByPage(Map<String, Object> filters, int offset, int limit);

    ResponseDto findById(Long id);

    void insert(ResponseDto dto);

    void update(ResponseDto dto);

    void deleteById(Long id);
}
