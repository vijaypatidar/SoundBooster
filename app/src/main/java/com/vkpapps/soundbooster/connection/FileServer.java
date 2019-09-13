/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkpapps.soundbooster.connection;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.vkpapps.soundbooster.connection.ClientHelper.TAG;

public class
FileServer extends Thread {

    private static ServerSocket serverSocket;
    private FileHandler myFileHandler;
    private String path;

    {
        try {
            serverSocket = new ServerSocket(15425);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileServer(String path) {
        this.path = path;
    }

    public static ServerSocket getServerSocket() {
        if (serverSocket == null) {
            new FileServer("");
        }
        return serverSocket;
    }

    @Override
    public void run() {
        for (int i = 0; i < Server.list.size(); i++) {
            try {
                Log.d(TAG, "run: waiting for client to receive file .... current ");
                Socket socket = serverSocket.accept();

                SendFile sendFile = new SendFile(socket, path);
                sendFile.startSending();

            } catch (IOException ignored) {
            }
        }

    }
}
