package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class InformClient implements Serializable {
    private boolean readyToReceive;

    public InformClient(boolean readyToReceive) {
        this.readyToReceive = readyToReceive;
    }

    public boolean isReadyToReceive() {
        return readyToReceive;
    }

    public void setReadyToReceive(boolean readyToReceive) {
        this.readyToReceive = readyToReceive;
    }
}
