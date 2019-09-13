package com.vkpapps.soundbooster.model;

import java.io.Serializable;
import java.util.Date;

public class SeekModel implements Serializable {
    private Date date;
    private int seekTo;

    public SeekModel(int seekTo, Date date) {
        this.seekTo = seekTo;
        this.date = date;
    }

    public int getSeekTo() {
        return seekTo;
    }

    public void setSeekTo(int seekTo) {
        this.seekTo = seekTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
