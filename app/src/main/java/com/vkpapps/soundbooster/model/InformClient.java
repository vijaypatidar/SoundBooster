package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class InformClient implements Serializable {
    private boolean readyToReceive;
    private String fileName;

    public InformClient(boolean readyToReceive, String fileName) {
        this.readyToReceive = readyToReceive;
        this.fileName = fileName;
    }

    public boolean isReadyToReceive() {
        return readyToReceive;
    }

    public void setReadyToReceive(boolean readyToReceive) {
        this.readyToReceive = readyToReceive;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
