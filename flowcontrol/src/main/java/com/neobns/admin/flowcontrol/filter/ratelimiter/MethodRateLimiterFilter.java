package com.neobns.admin.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class MethodRateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;

    public MethodRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        boolean isPresent = rateLimiterRegistry.find(uri).isPresent();
        if (isPresent) {
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(uri);

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
            } catch(RequestNotPermitted e){
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
                servletResponse.getWriter().flush();
            }
        } else {
            filterChain.doFilter(request, servletResponse);
        }
    }
}
