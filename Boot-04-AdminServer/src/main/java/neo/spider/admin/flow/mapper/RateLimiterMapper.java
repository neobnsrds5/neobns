package neo.spider.admin.flow.mapper;

import org.apache.ibatis.annotations.Mapper;
import spider.neo.solution.flowadmin.dto.ratelimiter.RateLimiterDto;
import spider.neo.solution.flowadmin.dto.ratelimiter.RateLimiterSearchDto;

import java.util.List;

@Mapper
public interface RateLimiterMapper {
    List<RateLimiterSearchDto> findByApplication(long id);
    int create(RateLimiterDto newRateLimiter);
    int delete(long id);
    RateLimiterDto findById(long id);
    int update(RateLimiterDto rl);
}
