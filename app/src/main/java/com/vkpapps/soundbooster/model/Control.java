package com.vkpapps.soundbooster.model;

import java.io.Serializable;

public class Control implements Serializable {
    public static final int PLAY = 1;
    public static final int STOP = 2;
    public static final int PAUSE = 3;
    public static final int NEXT = 4;
    public static final int PREVIOUS = 5;
    private int choice;
    private long time;

    public Control(int choice, long time) {
        this.choice = choice;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }
}
