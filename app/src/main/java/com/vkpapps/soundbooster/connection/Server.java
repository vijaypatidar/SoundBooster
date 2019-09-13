/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;

import com.vkpapps.soundbooster.handler.SignalHandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    static List<Socket> list = new ArrayList<>();
    private static Server server;
    private ServerSocket serverSocket;
    private SignalHandler signalHandler;

    private Server() {
        try {
            serverSocket = new ServerSocket(13595);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                System.out.println("waiting for new client...============================================== ");
                Socket socket = serverSocket.accept();
                ServerHelper serverHelper = new ServerHelper(socket, signalHandler);
                serverHelper.start();
                list.add(socket);
                if (signalHandler != null) {
                    Message message = new Message();
                    message.what = SignalHandler.NEW_DEVICE_CONNECTED;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", "New device add ");
                    message.setData(bundle);
                    signalHandler.sendMessage(message);
                }
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
//                Logger.getLogger(ServerHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void send(final Object msg, final Socket s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Socket socket = list.get(i);
                    if (socket.isConnected() && socket != s) {
                        OutputStream outputStream;
                        ObjectOutputStream objectOutputStream;
                        try {
                            outputStream = socket.getOutputStream();
                            objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                }
            }
        }).start();
    }


    public void setSignalHandler(SignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }
}
