package com.vkpapps.soundbooster.model;

import java.net.Socket;

public class FileRequest {
    private boolean isSend;
    private String name, path;
    private Socket socket;

    public FileRequest(boolean isSend, String name, String path, Socket socket) {
        this.isSend = isSend;
        this.name = name;
        this.path = path;
        this.socket = socket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
