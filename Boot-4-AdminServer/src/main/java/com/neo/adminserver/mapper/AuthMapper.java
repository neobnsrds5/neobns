package com.neo.adminserver.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.neo.adminserver.dto.FwkUserDTO;

@Mapper
public interface AuthMapper {
	
	List<FwkUserDTO> findById(@Param("username") String username);
	void updateUserSession(@Param("username") String username, @Param("sessionid") String sessionid, @Param("dtime") String dtime);
}
