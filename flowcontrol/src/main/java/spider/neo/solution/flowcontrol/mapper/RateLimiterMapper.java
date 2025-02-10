package spider.neo.solution.flowcontrol.mapper;

import spider.neo.solution.flowcontrol.dto.RateLimiterConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface RateLimiterMapper {
    RateLimiterConfigDto findById(long id);
    List<RateLimiterConfigDto> findAll(long application_id);
}
