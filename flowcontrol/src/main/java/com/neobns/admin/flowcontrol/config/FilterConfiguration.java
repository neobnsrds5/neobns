package com.neobns.admin.flowcontrol.config;

import com.neobns.admin.flowcontrol.filter.bulkhead.DetailBulkheadFilter;
//import com.neobns.admin.flowcontrol.filter.bulkhead.GlobalBulkheadFilter;
import com.neobns.admin.flowcontrol.filter.ratelimiter.GlobalRateLimiterFilter;
import com.neobns.admin.flowcontrol.filter.ratelimiter.DetailRateLimiterFilter;
import com.neobns.admin.flowcontrol.filter.ratelimiter.PersonalRateLimiterFilter;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    public FilterConfiguration(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

//    @Bean
//    public FilterRegistrationBean<GlobalBulkheadFilter> globalBulkheadFilter() {
//        FilterRegistrationBean<GlobalBulkheadFilter> registrationBean = new FilterRegistrationBean<>(new GlobalBulkheadFilter(bulkheadRegistry));
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }

    @Bean
    public FilterRegistrationBean<DetailBulkheadFilter> detailBulkheadFilter() {
        FilterRegistrationBean<DetailBulkheadFilter> registrationBean = new FilterRegistrationBean<>(new DetailBulkheadFilter(bulkheadRegistry));
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
        FilterRegistrationBean<DetailRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new DetailRateLimiterFilter(rateLimiterRegistry));
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