package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class Request implements Serializable {
    private String userId;
    private String name;

    public Request(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
