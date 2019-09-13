package com.vkpapps.soundbooster.model;

public class HostSong {
    private String path;
    private boolean available;

    public HostSong(String path, boolean available) {
        this.path = path;
        this.available = available;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
