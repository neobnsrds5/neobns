package com.neobns.admin.flowadmin.mapper;

import com.neobns.admin.flowadmin.dto.bulkhead.BulkheadDto;
import com.neobns.admin.flowadmin.dto.bulkhead.BulkheadSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    List<BulkheadSearchDto> findByApplication(long id);
    int create(BulkheadDto newBulkhead);
    int delete(long id);
    int update(BulkheadDto dto);
    BulkheadDto findById(long id);
}
