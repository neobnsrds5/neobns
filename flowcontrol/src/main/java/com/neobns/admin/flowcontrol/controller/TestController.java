package com.neobns.admin.flowcontrol.controller;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    public TestController(RateLimiterRegistry rateLimiterRegistry, BulkheadRegistry bulkheadRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @GetMapping("/test")
    @RateLimiter(name = "myServiceRateLimiter", fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> test() {
        System.out.println("success");
        return ResponseEntity.ok("success");
    }


    public ResponseEntity<String> fallbackMethod(RequestNotPermitted e) {
        System.out.println("fallbackMethod");
        return ResponseEntity.status(429).body("FAILED");
    }

    @GetMapping("/bulktest")
    @Bulkhead(name = "myServiceBulkhead", fallbackMethod = "bulkfall")
    public ResponseEntity<String> test2(){
        System.out.println("success");
        return ResponseEntity.ok("SUCCESS");
    }

    public ResponseEntity<String> bulkfall(BulkheadFullException e) {
        System.out.println("bulkfall");
        return ResponseEntity.status(429).body(e.getMessage());
    }

    @GetMapping("/ftrl")
    public ResponseEntity<String> test3(){
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/options")
    public ResponseEntity<String> options(){
        io.github.resilience4j.ratelimiter.RateLimiter rl_b = rateLimiterRegistry.rateLimiter("myServiceRateLimiter");
        io.github.resilience4j.bulkhead.Bulkhead bh_b = bulkheadRegistry.bulkhead("myServiceBulkhead");
        StringBuilder sb = new StringBuilder();
        sb.append("max concurrent calls: ").append(bh_b.getBulkheadConfig().getMaxConcurrentCalls()).append("\n");
        sb.append("limit for period: ").append(rl_b.getRateLimiterConfig().getLimitForPeriod()).append("\n");
        sb.append("limit refresh period: ").append(rl_b.getRateLimiterConfig().getLimitRefreshPeriod()).append("\n");
        sb.append("timeout duration: ").append(rl_b.getRateLimiterConfig().getTimeoutDuration());
        return ResponseEntity.ok(sb.toString());
    }
}
