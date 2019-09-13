package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class NewSongModel implements Serializable {
    private String name;
    private int port;

    public NewSongModel(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
