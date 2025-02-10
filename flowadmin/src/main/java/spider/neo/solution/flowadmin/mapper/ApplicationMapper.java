package spider.neo.solution.flowadmin.mapper;

import spider.neo.solution.flowadmin.dto.CreateApplicationDto;
import spider.neo.solution.flowadmin.dto.SearchApplicationResultDto;
import spider.neo.solution.flowadmin.dto.SearchDto;
import spider.neo.solution.flowadmin.dto.UpdateApplicationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplicationMapper {
    List<SearchApplicationResultDto> findSelective(SearchDto param, @Param("size") int size, @Param("offset") int offset);
    int countSelective(SearchDto param);
    int create(CreateApplicationDto param);
    int delete(long id);
    SearchApplicationResultDto findById(long id);
    long findByApplicationCategory(CreateApplicationDto param);
    int update(UpdateApplicationDto param);
    int updateModified_date(long id);
}
