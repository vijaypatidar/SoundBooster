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
    public static final int FILE_TYPE_MUSIC = 16;
    public static final int FILE_TYPE_PROFILE_PIC = 17;

    private int action;
    private String fileName;
    private String id;
    private int type;

    public ControlFile(int action, String fileName, String id, int type) {
        this.action = action;
        this.fileName = fileName;
        this.id = id;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
