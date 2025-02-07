package com.neobns.admin.flowcontrol.mapper;

import com.neobns.admin.flowcontrol.dto.ConfigDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper {
    ConfigDto findByAppName(String appName);
}
