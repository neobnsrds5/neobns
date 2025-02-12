package neo.spider.admin.flow.dto.redisPub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateConfigDto {
    //0 : bulkhead, 1: ratelimiter
    private int type;
    //0 : update/create, 1: delete
    private int doing;
    private long id;
    private String name;
}
