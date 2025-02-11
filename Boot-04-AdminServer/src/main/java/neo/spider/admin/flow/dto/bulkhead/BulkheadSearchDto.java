package neo.spider.admin.flow.dto.bulkhead;

import groovy.transform.Sealed;
import lombok.Getter;

@Getter
@Sealed
public class BulkheadSearchDto {
    private long bulkheadId;
    private String applicationName;
    private String url;
    private int maxConcurrentCalls;
    private int maxWaitDuration;
}
