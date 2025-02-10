package spider.neo.solution.flowcontrol;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigNameRegistry {

    private List<String> bulkheadNames;
    private List<String> rateLimiterNames;

    public ConfigNameRegistry() {
        bulkheadNames = new ArrayList<>();
        rateLimiterNames = new ArrayList<>();
    }

    public List<String> getBulkheadNames() {
        return bulkheadNames;
    }

    public List<String> getRateLimiterNames() {
        return rateLimiterNames;
    }

}
