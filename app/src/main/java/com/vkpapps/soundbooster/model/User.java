package com.vkpapps.soundbooster.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private long userId;
    private Bitmap bitmap;


    public User(String name, long userId, Bitmap bitmap) {
        this.name = name;
        this.userId = userId;
        this.bitmap = bitmap;
    }

    public User() {

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
