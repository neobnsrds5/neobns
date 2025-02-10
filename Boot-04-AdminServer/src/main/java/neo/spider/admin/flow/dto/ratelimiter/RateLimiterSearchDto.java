package neo.spider.admin.flow.dto.ratelimiter;

public class RateLimiterSearchDto {
    private long id;
    private String application_name;
    private int type;
    private String url;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeOutDuration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public void setLimitForPeriod(int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    public long getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    public void setLimitRefreshPeriod(long limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    public long getTimeOutDuration() {
        return timeOutDuration;
    }

    public void setTimeOutDuration(long timeOutDuration) {
        this.timeOutDuration = timeOutDuration;
    }
}
