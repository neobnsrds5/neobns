package com.neobns.admin.flowadmin.dto;

import java.time.LocalDate;

public class SearchApplicationResultDto {
    private long id;
    private String application_name;
    private LocalDate created_date;
    private LocalDate modified_date;

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

    @Override
    public String toString() {
        return "SearchApplicationResultDto{" +
                "id=" + id +
                ", application_name='" + application_name + '\'' +
                ", created_date=" + created_date +
                ", modified_date=" + modified_date +
                '}';
    }
}
