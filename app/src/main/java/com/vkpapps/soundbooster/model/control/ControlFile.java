package com.vkpapps.soundbooster.model.control;

import java.io.Serializable;

/***
 * @author VIJAY PATIDAR
 * */
public class ControlFile implements Serializable {
    public static final int UPLOAD_REQUEST = 12;
    public static final int DOWNLOAD_REQUEST = 13;
    public static final int UPLOAD_REQUEST_CONFIRM = 14;
    public static final int DOWNLOAD_REQUEST_CONFIRM = 15;

    private int action;
    private String fileName;
    private String id;

    public ControlFile(int action, String fileName, String id) {
        this.action = action;
        this.fileName = fileName;
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getData() {
        return fileName;
    }

    public void setData(String fileName) {
        this.fileName = fileName;
    }
}
