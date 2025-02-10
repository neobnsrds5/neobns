package spider.neo.solution.flowcontrol.config;

import spider.neo.solution.flowcontrol.ConfigNameRegistry;
import spider.neo.solution.flowcontrol.filter.bulkhead.DetailBulkheadFilter;
//import com.neobns.admin.flowcontrol.filter.bulkhead.GlobalBulkheadFilter;
import spider.neo.solution.flowcontrol.filter.ratelimiter.GlobalRateLimiterFilter;
import spider.neo.solution.flowcontrol.filter.ratelimiter.DetailRateLimiterFilter;
import spider.neo.solution.flowcontrol.filter.ratelimiter.PersonalRateLimiterFilter;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spider.neo.solution.flowcontrol.trie.TrieRegistry;

@Configuration
public class FilterConfiguration {

    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final TrieRegistry trieRegistry;

    public FilterConfiguration(BulkheadRegistry bulkheadRegistry,
                               RateLimiterRegistry rateLimiterRegistry,
                               ConfigNameRegistry configNameRegistry,
                               TrieRegistry trieRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.trieRegistry = trieRegistry;
    }

    @Bean
    public FilterRegistrationBean<DetailBulkheadFilter> detailBulkheadFilter() {
        FilterRegistrationBean<DetailBulkheadFilter> registrationBean = new FilterRegistrationBean<>(new DetailBulkheadFilter(bulkheadRegistry, trieRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<GlobalRateLimiterFilter> globalRateLimiterFilter() {
        FilterRegistrationBean<GlobalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new GlobalRateLimiterFilter(rateLimiterRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<DetailRateLimiterFilter> methodRateLimiterFilter() {
        FilterRegistrationBean<DetailRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new DetailRateLimiterFilter(rateLimiterRegistry, trieRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PersonalRateLimiterFilter> personalRateLimiterFilter(){
        FilterRegistrationBean<PersonalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new PersonalRateLimiterFilter(rateLimiterRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }
}