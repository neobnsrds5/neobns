package com.example.neobns.mapper;

import com.example.neobns.dto.ItemDto;
import com.example.neobns.dto.TransferDTO;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransferMapper {
    void insertTransferDetails(TransferDTO transferDTO);
}
