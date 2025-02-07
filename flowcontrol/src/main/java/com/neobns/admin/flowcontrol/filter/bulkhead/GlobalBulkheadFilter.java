//package com.neobns.admin.flowcontrol.filter.bulkhead;
//
//import io.github.resilience4j.bulkhead.Bulkhead;
//import io.github.resilience4j.bulkhead.BulkheadFullException;
//import io.github.resilience4j.bulkhead.BulkheadRegistry;
//import jakarta.servlet.*;
//
//import java.io.IOException;
//
//public class GlobalBulkheadFilter implements Filter {
//
//    private final BulkheadRegistry bulkheadRegistry;
//
//    public GlobalBulkheadFilter(BulkheadRegistry bulkheadRegistry) {
//        this.bulkheadRegistry = bulkheadRegistry;
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        boolean isGlobalPresent = bulkheadRegistry.find("global").isPresent();
//        System.out.println("1");
//        if (!isGlobalPresent) {
//            //어플리케이션의 모든 요청에 적용되는 벌크헤드 설정이 있지 않을 경우
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else {
//            //어플리케이션 전역에 적용되는 벌크헤드 설정이 있을 경우 적용한다.
//            Bulkhead bulkhead = bulkheadRegistry.bulkhead("global");
//
//            Runnable task = () -> {
//              try{
//                  filterChain.doFilter(servletRequest, servletResponse);
//              } catch(Exception e){
//                  throw new RuntimeException(e);
//              }
//            };
//
//            Runnable protectedTask = Bulkhead.decorateRunnable(bulkhead, task);
//            try {
//                protectedTask.run();
//            } catch (BulkheadFullException e){
//                servletResponse.setContentType("application/json");
//                servletResponse.setCharacterEncoding("UTF-8");
//                servletResponse.getWriter().write("{ \"error\": \"Too many concurrent requests. Please try again later.\" }");
//                servletResponse.getWriter().flush();
//            }
//        }
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
