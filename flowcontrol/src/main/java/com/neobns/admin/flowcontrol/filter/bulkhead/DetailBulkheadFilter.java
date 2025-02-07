package com.neobns.admin.flowcontrol.filter.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailBulkheadFilter implements Filter {

    private final BulkheadRegistry bulkheadRegistry;

    public DetailBulkheadFilter(BulkheadRegistry bulkheadRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        if (!uri.endsWith("/")){
            uri += "/";
        }

        List<String> names = new ArrayList<>();
        for (Bulkhead bk : bulkheadRegistry.getAllBulkheads()) {
            String name = bk.getName();
            if (name.endsWith("*")){
                String pattern = name.replace("*", ".*");
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(uri);
                if(m.find()){
                    names.add(name);
                }
            }
        }

        if (names.isEmpty()){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String name = names.stream().min(Comparator.comparingInt(String::length)).get();
            Bulkhead bulkhead = bulkheadRegistry.bulkhead(name);
            Runnable task = () -> {
                try{
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            };

            Runnable protectedTask = Bulkhead.decorateRunnable(bulkhead, task);
            try {
                protectedTask.run();
            } catch (BulkheadFullException e){
                HttpServletResponse resp = (HttpServletResponse) servletResponse;
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
                resp.getWriter().flush();
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
