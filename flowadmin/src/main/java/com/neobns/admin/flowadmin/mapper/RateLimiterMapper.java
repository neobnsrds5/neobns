package com.neobns.admin.flowadmin.mapper;

import com.neobns.admin.flowadmin.dto.ratelimiter.RateLimiterDto;
import com.neobns.admin.flowadmin.dto.ratelimiter.RateLimiterSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RateLimiterMapper {
    List<RateLimiterSearchDto> findByApplication(long id);
    int create(RateLimiterDto newRateLimiter);
    int delete(long id);
    RateLimiterDto findById(long id);
    int update(RateLimiterDto rl);
}
