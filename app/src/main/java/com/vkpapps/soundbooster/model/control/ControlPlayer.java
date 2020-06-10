package com.vkpapps.soundbooster.model.control;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ControlPlayer implements Serializable {
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_PREVIOUS = 4;
    public static final int ACTION_SEEK_TO = 6;
    public static final int ACTION_CHANGE_VOLUME = 7;

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

    public void copyToBundle(@NonNull Bundle bundle) {
        bundle.putInt("action", action);
        bundle.putString("data", data);
        bundle.putInt("intData", intData);
    }
}
