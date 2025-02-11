package spider.neo.solution.flowcontrol.mapper;

import spider.neo.solution.flowcontrol.dto.BulkheadDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BulkheadMapper {
    BulkheadDto findById(long id);
    List<BulkheadDto> findAll(long application_id);
}
