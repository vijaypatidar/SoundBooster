package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommandHelperRunnable implements Runnable {
    public User user;
    private OutputStream outputStream;
    private SignalHandler signalHandler;
    private Socket socket;
    public String id;

    public CommandHelperRunnable(Socket socket, @NonNull SignalHandler signalHandler, User user) {
        this.socket = socket;
        this.signalHandler = signalHandler;
        this.id = System.currentTimeMillis() + "";
        this.user = user;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection established");
            InputStream inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            String IDCom = "ID " + user.getName() + "," + user.getUserId();
            outputStream.write(IDCom.getBytes());
            outputStream.flush();
            byte[] bytes = new byte[2048];
            String command;
            while (socket.isConnected()) {
                int read = inputStream.read(bytes);
                command = new String(bytes, 0, read);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("command", command);
                bundle.putString("id", id);
                message.setData(bundle);
                signalHandler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String command) {
        new Thread(() -> {
            try {
                outputStream.write(command.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
