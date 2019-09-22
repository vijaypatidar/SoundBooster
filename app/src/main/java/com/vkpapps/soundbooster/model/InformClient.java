package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class InformClient implements Serializable {
    private boolean readyToReceive;
    private String file;

    public InformClient(boolean readyToReceive, String file) {
        this.readyToReceive = readyToReceive;
        this.file = file;
    }

    public boolean isReadyToReceive() {
        return readyToReceive;
    }

    public void setReadyToReceive(boolean readyToReceive) {
        this.readyToReceive = readyToReceive;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
