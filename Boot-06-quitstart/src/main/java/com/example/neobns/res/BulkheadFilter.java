package com.example.neobns.res;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BulkheadFilter extends OncePerRequestFilter {

    private final BulkheadRegistry bulkheadRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("globalBulkhead");

        if (bulkhead.tryAcquirePermission()){
            try{
                filterChain.doFilter(request, response);
            } finally {
                bulkhead.onComplete();
            }
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Service is overloaded. Please try again later.");
        }
    }
}
