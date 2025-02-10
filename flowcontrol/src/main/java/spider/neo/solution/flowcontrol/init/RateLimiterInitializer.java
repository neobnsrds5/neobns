package spider.neo.solution.flowcontrol.init;

import spider.neo.solution.flowcontrol.ConfigurationProp;
import spider.neo.solution.flowcontrol.dto.RateLimiterConfigDto;
import spider.neo.solution.flowcontrol.mapper.ApplicationMapper;
import spider.neo.solution.flowcontrol.mapper.RateLimiterMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import spider.neo.solution.flowcontrol.trie.TrieRegistry;

import java.util.List;

@Component
public class RateLimiterInitializer {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final ApplicationMapper applicationMapper;
    private final RateLimiterMapper rateLimiterMapper;
    private final ConfigurationProp prop;
    private final TrieRegistry trieRegistry;

    public RateLimiterInitializer(RateLimiterRegistry rateLimiterRegistry,
                                  ApplicationMapper applicationMapper,
                                  RateLimiterMapper rateLimiterMapper,
                                  ConfigurationProp prop,
                                  TrieRegistry trieRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.applicationMapper = applicationMapper;
        this.rateLimiterMapper = rateLimiterMapper;
        this.prop = prop;
        this.trieRegistry = trieRegistry;
    }

    @PostConstruct
    public void init(){
        // 어플리케이션이 현재 등록되어 있지 않은 경우
        // 읽어올 값 역시 없을 테니 즉시 종료한다.
        if (applicationMapper.count(prop.getName()) == 0){
            return;
        }
        long id = applicationMapper.findIdByName(prop.getName());
        List<RateLimiterConfigDto> rateLimiters = rateLimiterMapper.findAll(id);
        for (RateLimiterConfigDto rateLimiter : rateLimiters) {
            RateLimiterConfig newConfig = RateLimiterConfig.custom()
                    .timeoutDuration(java.time.Duration.ofSeconds(rateLimiter.getTimeoutDuration()))
                    .limitRefreshPeriod(java.time.Duration.ofSeconds(rateLimiter.getLimitRefreshPeriod()))
                    .limitForPeriod(rateLimiter.getLimitForPeriod())
                    .build();
            rateLimiterRegistry.rateLimiter(rateLimiter.getUrl(), newConfig);
            trieRegistry.getRateLimiterTrie().insert(rateLimiter.getUrl());
        }
    }
}
