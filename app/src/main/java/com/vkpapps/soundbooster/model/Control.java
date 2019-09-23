package com.vkpapps.soundbooster.model;


import java.io.Serializable;

public class Control implements Serializable {
    public static final int PLAY = 1;
    public static final int PAUSE = 3;
    public static final int SEEK = 6;
    private int choice;
    private long time;
    private int value;
    private String name;

    public Control(int choice, long time, int value) {
        this.choice = choice;
        this.time = time;
        this.value = value;
    }

    public Control(int choice, long time, String name) {
        this.choice = choice;
        this.time = time;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
