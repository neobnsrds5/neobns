package com.neobns.admin.flowadmin.mapper;

import com.neobns.admin.flowadmin.dto.ConfigDto;
import com.neobns.admin.flowadmin.dto.SearchDto;
import com.neobns.admin.flowadmin.dto.SearchResultDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfigMapper {
    List<SearchResultDto> findSelective(@Param("param")SearchDto param,
                                        @Param("limit")int limit,
                                        @Param("offset")int offset);

    int create(ConfigDto param);
    int delete(Long id);
    ConfigDto findById(Long id);
    int update(ConfigDto param);
}
