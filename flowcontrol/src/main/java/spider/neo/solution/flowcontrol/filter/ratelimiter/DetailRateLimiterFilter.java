package spider.neo.solution.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spider.neo.solution.flowcontrol.trie.TrieRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailRateLimiterFilter implements Filter {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final TrieRegistry trieRegistry;

    public DetailRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry,
                                   TrieRegistry trieRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.trieRegistry = trieRegistry;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();

        String name = trieRegistry.getRateLimiterTrie().search(uri);

        if (name == null){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            RateLimiter rl = rateLimiterRegistry.rateLimiter(name);
            if (rl.acquirePermission()){
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse resp = (HttpServletResponse) servletResponse;
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
                resp.getWriter().flush();
            }
        }
    }
}
