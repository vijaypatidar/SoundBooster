package com.vkpapps.soundbooster.connection;

import android.util.Log;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHelper extends Thread implements OnClientConnectionStateListener {
    private SignalHandler signalHandler;
    private ArrayList<ClientHelper> clientHelpers;
    private User user;
    private OnClientConnectionStateListener onClientConnectionStateListener;

    public ServerHelper(SignalHandler signalHandler, User user, OnClientConnectionStateListener onClientConnectionStateListener) {
        clientHelpers = new ArrayList<>();
        this.signalHandler = signalHandler;
        this.user = user;
        this.onClientConnectionStateListener = onClientConnectionStateListener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(1203);
                Socket socket = serverSocket.accept();
                ClientHelper commandHelper = new ClientHelper(socket, signalHandler, user, this);
                new Thread(commandHelper).start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommand(Object command) {
        for (ClientHelper c : clientHelpers) {
            c.write(command);
        }
    }

    public void sendCommandToOnly(Object command, String clientId) {
        for (ClientHelper c : clientHelpers) {
            c.writeIfClient(command, clientId);
        }
    }

    public ArrayList<ClientHelper> getClientHelpers() {
        return clientHelpers;
    }

    @Override
    public void onClientConnected(ClientHelper clientHelper) {
        clientHelpers.add(clientHelper);
        Log.d("vijay", "onClientConnected: " + clientHelper + "  " + clientHelpers.size());
        if (onClientConnectionStateListener != null) {
            onClientConnectionStateListener.onClientConnected(clientHelper);
        }
    }

    @Override
    public void onClientDisconnected(ClientHelper clientHelper) {
        Log.d("vijay", "onClientDisconnected: " + clientHelper + "  " + clientHelpers.size());
        clientHelpers.remove(clientHelper);
        if (onClientConnectionStateListener != null) {
            onClientConnectionStateListener.onClientDisconnected(clientHelper);
        }
    }
}
