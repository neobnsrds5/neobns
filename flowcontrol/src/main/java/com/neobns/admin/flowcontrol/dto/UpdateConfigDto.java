package com.neobns.admin.flowcontrol.dto;

public class UpdateConfigDto {
    //0 : bulkhead, 1: ratelimiter
    private int type;
    //0 : update/create, 1: delete
    private int doing;
    private long id;
    private String name;

    @Override
    public String toString() {
        return "UpdateConfigDto{" +
                "type=" + type +
                ", doing=" + doing +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDoing() {
        return doing;
    }

    public void setDoing(int doing) {
        this.doing = doing;
    }
}
