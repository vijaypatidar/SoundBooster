package com.vkpapps.soundbooster.model;

import com.vkpapps.soundbooster.connection.ClientHelper;

public class FileRequest {
    private boolean send;
    private ClientHelper client;
    private String name;

    public FileRequest(boolean send, ClientHelper client, String name) {
        this.send = send;
        this.client = client;
        this.name = name;
    }


}
