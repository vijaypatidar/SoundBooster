package com.vkpapps.soundbooster.model.control;

import java.io.Serializable;

public class ControlPlayer implements Serializable {
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_PREVIOUS = 4;

    private int action;
    private String data;

    public ControlPlayer(int action, String data) {
        this.action = action;
        this.data = data;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
