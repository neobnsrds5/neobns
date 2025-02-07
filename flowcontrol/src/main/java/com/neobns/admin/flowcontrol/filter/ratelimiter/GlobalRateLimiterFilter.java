package com.neobns.admin.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;

import java.io.IOException;

public class GlobalRateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;

    public GlobalRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean isPresent = rateLimiterRegistry.find("global").isPresent();
        if (!isPresent){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("global");
            Runnable task = () -> {
                try{
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            };

            Runnable protectedTask = RateLimiter.decorateRunnable(rateLimiter, task);
            try {
                protectedTask.run();
            } catch (RequestNotPermitted e){
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
                servletResponse.getWriter().flush();
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
