package com.vkpapps.soundbooster.model;

import java.io.Serializable;
import java.util.Date;

public class Control implements Serializable {
    public static final int PLAY = 1;
    public static final int STOP = 2;
    public static final int PAUSE = 3;
    public static final int NEXT = 4;
    public static final int PREVIOUS = 5;
    private int choice;
    private Date date;

    public Control(int choice, Date date) {
        this.choice = choice;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }
}
