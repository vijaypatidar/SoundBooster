package com.vkpapps.soundbooster.model.control;

import java.io.Serializable;

public class ControlFile implements Serializable {
    public static final int ACTION_SEND = 1;
    public static final int ACTION_RECEIVE = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_PREVIOUS = 4;

    private int action;
    private String data;

    public ControlFile(int action, String data) {
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
