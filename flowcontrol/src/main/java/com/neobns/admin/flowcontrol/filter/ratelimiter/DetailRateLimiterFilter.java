package com.neobns.admin.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailRateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;

    public DetailRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        if(!uri.endsWith("/")){
            uri += "/";
        }

        List<String> names = new ArrayList<>();
        for (RateLimiter rateLimiter : rateLimiterRegistry.getAllRateLimiters()) {
            String name = rateLimiter.getName();
            String pattern = name.replace("*", ".*");
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(uri);
            if (m.find()) {
                names.add(name);
            }
        }

        if (names.isEmpty()){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String name = names.stream().min(Comparator.comparingInt(String::length)).get();
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(name);
            if (rateLimiter.acquirePermission()){
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
                servletResponse.getWriter().flush();
            }
        }
    }
}
