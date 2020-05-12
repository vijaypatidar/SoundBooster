package com.vkpapps.soundbooster.model.control;

import java.io.Serializable;

public class ControlFile implements Serializable {
    public static final int ACTION_SEND_REQUEST = 12;
    public static final int ACTION_RECEIVE_REQUEST = 13;
    public static final int ACTION_SEND_CONFIRM = 14;
    public static final int ACTION_RECEIVE_CONFIRM = 15;

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
