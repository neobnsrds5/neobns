package neo.spider.admin.flow.mapper;

import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RateLimiterMapper {
    List<RateLimiterSearchDto> findByApplication(long applicationId);
    int create(RateLimiterDto newRateLimiter);
    int delete(long ratelimiterId);
    RateLimiterDto findById(long ratelimiterId);
    int update(RateLimiterDto rl);
}
