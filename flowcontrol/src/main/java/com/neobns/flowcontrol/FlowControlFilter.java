package com.neobns.flowcontrol;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@WebFilter("/*")
public class FlowControlFilter implements Filter {

    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    public FlowControlFilter(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            Bulkhead bulkhead = bulkheadRegistry.bulkhead("myServiceBulkhead");
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("myServiceRateLimiter");

            Runnable task = () -> {
                try{
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable protectedTask = Bulkhead.decorateRunnable(bulkhead, RateLimiter.decorateRunnable(rateLimiter, task));
            protectedTask.run();

        } catch (Exception e){
            servletResponse.setContentType("application/json");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
            servletResponse.getWriter().flush();
        }
    }

}
