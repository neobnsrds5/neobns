package com.neobns.flowcontrol;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/*")
public class BulkheadFilter implements Filter {

    private final BulkheadRegistry bulkheadRegistry;

    public BulkheadFilter(final BulkheadRegistry bulkheadRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try{
            Bulkhead bulkhead = bulkheadRegistry.bulkhead("myServiceBulkhead");
            Bulkhead.decorateRunnable(bulkhead, () -> {
                try{
                    filterChain.doFilter(servletRequest, servletResponse);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            }).run();
        } catch (Exception e){
            servletResponse.setContentType("application/json");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
            servletResponse.getWriter().flush();
        }
    }

}
