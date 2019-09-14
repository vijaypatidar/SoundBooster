package com.vkpapps.soundbooster.model;

public class HostSong {
    private String path, name;
    private boolean available;

    public HostSong(String path, String name, boolean available) {
        this.path = path;
        this.name = name;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
