package spider.neo.solution.flowcontrol.mapper;

import spider.neo.solution.flowcontrol.dto.RateLimiterDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RateLimiterMapper {
    RateLimiterDto findById(long id);
    List<RateLimiterDto> findAll(long application_id);
}
