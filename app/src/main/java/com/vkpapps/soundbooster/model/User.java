package com.vkpapps.soundbooster.model;

import java.io.Serializable;

/**
 * @author VIJAY PATIDAR
 * */
public class User implements Serializable {
    private String name;
    private String userId;
    private boolean access;

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public User() {

    }

    public User(String name, String userId, boolean access) {
        this.name = name;
        this.userId = userId;
        this.access = access;
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
