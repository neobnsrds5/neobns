package com.neobns.admin.flowcontrol.filter.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class DetailBulkheadFilter implements Filter {

    private final BulkheadRegistry bulkheadRegistry;

    public DetailBulkheadFilter(BulkheadRegistry bulkheadRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        System.out.println("URI: " + uri);

        boolean isPresent = bulkheadRegistry.find(uri).isPresent();

        if (!isPresent) {
            filterChain.doFilter(request, servletResponse);
        } else {
            Bulkhead bulkhead = bulkheadRegistry.bulkhead(uri);
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
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
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
