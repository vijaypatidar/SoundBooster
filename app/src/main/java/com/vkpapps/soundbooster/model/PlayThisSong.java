package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class PlayThisSong implements Serializable {
    private String name;
    private long atTime;

    public PlayThisSong(String name, long atTime) {
        this.name = name;
        this.atTime = atTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAtTime() {
        return atTime;
    }

    public void setAtTime(long atTime) {
        this.atTime = atTime;
    }
}
