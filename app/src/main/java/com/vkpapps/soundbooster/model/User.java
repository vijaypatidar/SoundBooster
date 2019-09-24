package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String userId;
    private boolean sharingAllowed;


    public User(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public User() {

    }

    public boolean isSharingAllowed() {
        return sharingAllowed;
    }

    public void setSharingAllowed(boolean sharingAllowed) {
        this.sharingAllowed = sharingAllowed;
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
