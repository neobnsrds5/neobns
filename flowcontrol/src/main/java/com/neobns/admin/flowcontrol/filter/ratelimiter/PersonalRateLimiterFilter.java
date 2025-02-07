package com.neobns.admin.flowcontrol.filter.ratelimiter;


import com.github.benmanes.caffeine.cache.Cache;
import com.neobns.admin.flowcontrol.UserRateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class PersonalRateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;

    public PersonalRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String id = httpRequest.getHeader("id");

        Cache<String, RateLimiter> cache = UserRateLimiterRegistry.getInstance().getCache();
        if (id == null){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            if(rateLimiterRegistry.find("personal").isPresent()){
                RateLimiter rateLimiter = rateLimiterRegistry.find("personal").get();
                RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
                RateLimiter userRateLimiter = cache.getIfPresent(id);
                if (userRateLimiter == null) {
                    userRateLimiter = RateLimiter.of(id, config);
                    cache.put(id, userRateLimiter);
                }
                Runnable task = () -> {
                    try{
                        filterChain.doFilter(servletRequest, servletResponse);

                    } catch(Exception e){
                        throw new RuntimeException(e);
                    }
                };
                Runnable protectedTask = RateLimiter.decorateRunnable(userRateLimiter, task);
                try {
                    protectedTask.run();
                } catch(RequestNotPermitted e){
                    servletResponse.setContentType("application/json");
                    servletResponse.setCharacterEncoding("UTF-8");
                    servletResponse.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
                    servletResponse.getWriter().flush();
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
