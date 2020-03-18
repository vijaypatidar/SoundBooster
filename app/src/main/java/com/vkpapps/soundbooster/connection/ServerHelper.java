package com.vkpapps.soundbooster.connection;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHelper extends Thread {
    private SignalHandler signalHandler;
    private ArrayList<ClientHelper> clientHelpers;
    private User user;
    private OnClientConnectionStateListener onClientConnectionStateListener;

    public ServerHelper(SignalHandler signalHandler, User user, OnClientConnectionStateListener onClientConnectionStateListener) {
        this.signalHandler = signalHandler;
        this.user = user;
        this.onClientConnectionStateListener = onClientConnectionStateListener;
        clientHelpers = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(1203);
                Socket socket = serverSocket.accept();
                ClientHelper commandHelper = new ClientHelper(socket, signalHandler, user, onClientConnectionStateListener);
                clientHelpers.add(commandHelper);
                new Thread(commandHelper).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommand(String command) {
        for (ClientHelper c : clientHelpers) {
            c.write(command);
        }
    }

    public void sendCommandToOnly(String command, String clientId) {
        for (ClientHelper c : clientHelpers) {
            if (c.id.equals(clientId)) c.write(command);
        }
    }

    public void setUser(User tmp, String id) {
        for (ClientHelper c : clientHelpers) {
            if (c.id.equals(id)) {
                c.user = tmp;
            }
        }
    }

    public ArrayList<ClientHelper> getClientHelpers() {
        return clientHelpers;
    }
}
