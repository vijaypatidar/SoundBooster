/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkpapps.soundbooster.connection;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.InformClient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    public static HashMap<String, Socket> socketHashMap = new HashMap<>();
    static List<Socket> list = new ArrayList<>();
    private static Server server;
    private ServerSocket serverSocket;
    private SignalHandler signalHandler;
    private boolean run = true;

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
        while (!serverSocket.isClosed() && run) {
            try {
                System.out.println("waiting for new client...============================================== ");
                Socket socket = serverSocket.accept();
                ServerHelper serverHelper = new ServerHelper(socket, signalHandler);
                serverHelper.start();

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

    public void informClient(final Socket socket, final InformClient informClient) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket.isConnected()) {
                    OutputStream outputStream;
                    ObjectOutputStream objectOutputStream;
                    try {
                        outputStream = socket.getOutputStream();
                        objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(informClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void setSignalHandler(SignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }

    public void stopServer() {
        run = false;
        server = null;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
