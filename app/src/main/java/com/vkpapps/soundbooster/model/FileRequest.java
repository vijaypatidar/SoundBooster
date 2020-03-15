package com.vkpapps.soundbooster.model;

import com.vkpapps.soundbooster.connection.CommandHelperRunnable;

public class FileRequest {
    private boolean send;
    private CommandHelperRunnable client;
    private String name;

    public FileRequest(boolean send, CommandHelperRunnable client, String name) {
        this.send = send;
        this.client = client;
        this.name = name;
    }


}
