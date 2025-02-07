package com.neobns.admin.flowadmin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.admin.flowadmin.dto.ConfigDto;
import com.neobns.admin.flowadmin.dto.SearchDto;
import com.neobns.admin.flowadmin.dto.SearchResultDto;
import com.neobns.admin.flowadmin.mapper.ConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControlService {

    private final ConfigMapper configMapper;
    private final MessagePublisher publisher;
    private final ObjectMapper objectMapper;

    public ControlService(ConfigMapper configMapper, MessagePublisher publisher, ObjectMapper objectMapper) {
        this.configMapper = configMapper;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    public List<SearchResultDto> find(SearchDto dto, int page, int size){
        int offset = (page - 1) * size;
        return configMapper.findSelective(dto, size, offset);
    }

    public int create(ConfigDto dto){
        try {
            int result = configMapper.create(dto);
            if (result > 0) {
                try {
                    String json = objectMapper.writeValueAsString(dto);
                    publisher.publish(dto.getAppName(), json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByIds(List<Long> ids){
        try{
            int result = 0;
            for (Long id : ids){
                result += configMapper.delete(id);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ConfigDto findById(Long id){
        try{
            return configMapper.findById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int update(ConfigDto dto){
        try {
            int result =  configMapper.update(dto);
            if (result > 0) {
                String json = objectMapper.writeValueAsString(dto);
                System.out.println(json);
                publisher.publish(dto.getAppName(), json);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
