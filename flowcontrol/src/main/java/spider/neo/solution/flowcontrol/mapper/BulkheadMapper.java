package spider.neo.solution.flowcontrol.mapper;

import spider.neo.solution.flowcontrol.dto.BulkheadConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    BulkheadConfigDto findById(long id);
    List<BulkheadConfigDto> findAll(long application_id);
}
