package neo.spider.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import neo.spider.admin.dto.AdminApiDTO;

@Mapper
public interface AdminMapper {

	List<AdminApiDTO> findAll(@Param("mockApiName") String apiName, @Param("size") int size, @Param("offset") int offset);
	
	int countByApiName(@Param("mockApiName") String apiName);
	
}
