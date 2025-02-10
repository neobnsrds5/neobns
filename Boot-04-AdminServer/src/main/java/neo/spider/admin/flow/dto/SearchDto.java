package neo.spider.admin.flow.dto;

import java.time.LocalDate;

public class SearchDto {
    private String id;
    private String application_name;
    private LocalDate created_date;
    private LocalDate modified_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public LocalDate getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDate created_date) {
        this.created_date = created_date;
    }

    public LocalDate getModified_date() {
        return modified_date;
    }

    public void setModified_date(LocalDate modified_date) {
        this.modified_date = modified_date;
    }

    public String toQueryString(){
        return "id=" + id + "&" +
                "application=" + application_name + "&" +
                "created_date=" + created_date + "&" +
                "modified_date=" + modified_date + "&";
    }
}
