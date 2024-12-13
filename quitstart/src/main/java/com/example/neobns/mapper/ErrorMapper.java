package com.example.neobns.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.neobns.dto.ErrorLogDTO;

@Mapper
public interface ErrorMapper {
	List<ErrorLogDTO> getRecord(Long duration);
}
