package com.vkpapps.soundbooster.model;

import java.io.Serializable;

/**
 * @author VIJAY PATIDAR
 * */
public class User implements Serializable {
    private String name;
    private String userId;

    public User() {

    }

    public User(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
