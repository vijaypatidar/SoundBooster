package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class SeekModel implements Serializable {
    private long time;
    private int seekTo;

    public SeekModel(long time, int seekTo) {
        this.time = time;
        this.seekTo = seekTo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSeekTo() {
        return seekTo;
    }

    public void setSeekTo(int seekTo) {
        this.seekTo = seekTo;
    }
}
