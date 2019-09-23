package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class Reaction implements Serializable {
    private boolean like;
    private String userId;

    public Reaction(boolean like, String userId) {
        this.like = like;
        this.userId = userId;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
