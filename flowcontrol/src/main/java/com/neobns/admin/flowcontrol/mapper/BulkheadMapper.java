package com.neobns.admin.flowcontrol.mapper;

import com.neobns.admin.flowcontrol.dto.BulkheadConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    List<BulkheadConfigDto> findByApplication(String application);
}
