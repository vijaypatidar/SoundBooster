package com.vkpapps.soundbooster.model;

import java.io.Serializable;
import java.util.Date;

public class PlayThisSong implements Serializable {
    private String name;
    private Date atTime;

    public PlayThisSong(String name, Date atTime) {
        this.name = name;
        this.atTime = atTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAtTime() {
        return atTime;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }
}
