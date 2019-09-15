/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.SeekModel;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerHelper extends Thread {
    private Socket socket;
    private SignalHandler signalHandler;

    ServerHelper(Socket socket, SignalHandler signalHandler) {
        this.socket = socket;
        this.signalHandler = signalHandler;
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                ObjectInputStream objectInputStream;
                InputStream inputStream = socket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                Object object = objectInputStream.readObject();
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                if (object instanceof SeekModel) {
                    message.what = SignalHandler.NEW_SEEK_REQUEST;
                    bundle.putSerializable("data", (SeekModel) object);
                    Server.getInstance().send(object, socket);
                } else if (object instanceof Request) {
                    message.what = SignalHandler.NEW_SONG_REQUEST;
                    bundle.putSerializable("data", (Request) object);
                    Server.getInstance().send(object, socket);
                } else if (object instanceof Control) {
                    message.what = SignalHandler.NEW_CONTROL_REQUEST;
                    bundle.putSerializable("data", (Control) object);
                    Server.getInstance().send(object, socket);
                } else if (object instanceof PlayThisSong) {
                    message.what = SignalHandler.SONG_PLAY_REQUEST;
                    bundle.putSerializable("data", (PlayThisSong) object);
                    Server.getInstance().send(object, socket);
                } else if (object instanceof User) {
                    User user = (User) object;
                    message.what = SignalHandler.NEW_DEVICE_CONNECTED;
                    bundle.putSerializable("data", user);
                    Server.list.add(socket);
                    Server.socketHashMap.put(user.getUserId(), socket);
                }
                signalHandler.sendMessage(message);
            } catch (IOException | ClassNotFoundException ignored) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
