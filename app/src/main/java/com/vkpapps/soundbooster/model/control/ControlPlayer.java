package com.vkpapps.soundbooster.model.control;

import java.io.Serializable;

/**
 * @author VIJAY PATIDAR
 * */
public class ControlPlayer implements Serializable {
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_PREVIOUS = 4;
    public static final int ACTION_SEEK_TO = 6;
    public static final int ACTION_CHANGE_VOLUME = 7;
    public static final int ACTION_RESUME = 8;

    private int action;
    private String data;
    private int intData;


    public ControlPlayer(int action, String data) {
        this.action = action;
        this.data = data;
    }

    public ControlPlayer() {

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

    public int getIntData() {
        return intData;
    }

    public void setIntData(int intData) {
        this.intData = intData;
    }

}
