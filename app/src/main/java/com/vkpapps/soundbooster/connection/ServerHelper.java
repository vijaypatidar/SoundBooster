package com.vkpapps.soundbooster.connection;

import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnControlRequestListener;
import com.vkpapps.soundbooster.interfaces.OnObjectReceiveListener;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * @author VIJAY PATIDAR
 * */
public class ServerHelper extends Thread implements OnClientConnectionStateListener, OnObjectReceiveListener {
    private OnControlRequestListener onControlRequestListener;
    private ArrayList<ClientHelper> clientHelpers;
    private User user;
    private OnClientConnectionStateListener onClientConnectionStateListener;
    private boolean live = true;

    public ServerHelper(OnControlRequestListener onControlRequestListener, User user, OnClientConnectionStateListener onClientConnectionStateListener) {
        clientHelpers = new ArrayList<>();
        this.onControlRequestListener = onControlRequestListener;
        this.user = user;
        this.onClientConnectionStateListener = onClientConnectionStateListener;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1203);
            while (live) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHelper commandHelper = new ClientHelper(socket, onControlRequestListener, user, this);
                    commandHelper.setOnObjectReceiveListener(this);
                    commandHelper.start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(Object command) {
        for (ClientHelper c : clientHelpers) {
            c.write(command);
        }
    }

    public void sendCommandToOnly(Object command, String clientId) {
        for (ClientHelper c : clientHelpers) {
            if (c.user.getUserId().endsWith(clientId)) {
                c.write(command);
            }
        }
    }

    public ArrayList<ClientHelper> getClientHelpers() {
        return clientHelpers;
    }

    @Override
    public void onClientConnected(ClientHelper clientHelper) {
        clientHelpers.add(clientHelper);
        if (onClientConnectionStateListener != null) {
            onClientConnectionStateListener.onClientConnected(clientHelper);
        }
    }

    @Override
    public void onClientDisconnected(ClientHelper clientHelper) {
        clientHelpers.remove(clientHelper);
        if (onClientConnectionStateListener != null) {
            onClientConnectionStateListener.onClientDisconnected(clientHelper);
        }
    }

    @Override
    public void onObjectReceive(Object object) {
        broadcast(object);
    }

    public void shutDown() {
        live = false;
        for (ClientHelper c : clientHelpers) {
            c.shutDown();
        }
    }
}
