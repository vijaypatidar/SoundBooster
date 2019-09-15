/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.SeekModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHelper extends Thread {

    static final String TAG = "vijay";
    private Socket socket;
    private String host;
    private SignalHandler signalHandler;
    private boolean run = true;

    public ClientHelper(String host, SignalHandler signalHandler) {
        this.host = host;
        this.signalHandler = signalHandler;
    }

    private void connect() {
        socket = new Socket();
        SocketAddress sa = new InetSocketAddress(host, 13595);
        try {
            Log.d(TAG, "connect: connecting to server...");
            socket.connect(sa, 5500);
            signalHandler.sendEmptyMessage(SignalHandler.CONNECT_TO_HOST);
        } catch (IOException ex) {
            Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        connect();
        while (socket.isConnected() && run) {

            try {
                ObjectInputStream objectInputStream;
                InputStream inputStream = socket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                Object object = objectInputStream.readObject();
                Message message = new Message();
                Bundle bundle = new Bundle();

                if (object instanceof SeekModel) {
                    message.what = SignalHandler.NEW_SEEK_REQUEST;
                    bundle.putSerializable("data", (SeekModel) object);
                } else if (object instanceof Request) {
                    message.what = SignalHandler.NEW_SONG_REQUEST;
                    bundle.putSerializable("data", (Request) object);
                } else if (object instanceof Control) {
                    message.what = SignalHandler.NEW_CONTROL_REQUEST;
                    bundle.putSerializable("data", (Control) object);
                } else if (object instanceof PlayThisSong) {
                    message.what = SignalHandler.SONG_PLAY_REQUEST;
                    bundle.putSerializable("data", (PlayThisSong) object);
                } else if (object instanceof InformClient) {
                    message.what = SignalHandler.HANDLE_REQUEST;
                    bundle.putSerializable("data", (InformClient) object);
                }

                message.setData(bundle);
                signalHandler.sendMessage(message);

            } catch (IOException | ClassNotFoundException ex) {
//                Logger.getLogger(ClientHelper.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(final Object msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send: ================================ msg " + msg);
                OutputStream outputStream;
                ObjectOutputStream objectOutputStream;
                if (socket.isConnected()) {
                    try {
                        outputStream = socket.getOutputStream();
                        objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    public void stopClientHelper() {
        run = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
