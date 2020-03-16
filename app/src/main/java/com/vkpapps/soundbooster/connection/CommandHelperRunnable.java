package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

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
        this.user = user;
    }

    @Override
    public void run() {
        String command;
        Bundle bundle = new Bundle();
        try {
            System.out.println("Connection established");
            InputStream inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            // send identity to connected device
            String IDCom = "ID " + user.getName() + "," + user.getUserId();
            outputStream.write(IDCom.getBytes());
            outputStream.flush();
            Thread.sleep(2000);
            byte[] bytes = new byte[2048];

            // read identity of connected device
            int read1 = inputStream.read(bytes);
            String res = new String(bytes, 0, read1);
            String[] strings = res.split(",");
            id = strings[1];
            user = new User(strings[0], id);
            command = "DC " + id;
            Log.d("CONTROLS", "run:  =============== " + user.getName() + "  id " + id);
            bundle.putString("ID", id);
            while (socket.isConnected()) {
                Message message = new Message();
                bundle.putString("command", command);
                message.setData(bundle);
                signalHandler.sendMessage(message);
                int read = inputStream.read(bytes);
                command = new String(bytes, 0, read);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // NOTIFY DEVICE DISCONNECT
        command = "DD " + id;
        bundle.putString("command", command);
        Message message = new Message();
        message.setData(bundle);
        signalHandler.sendMessage(message);
        Log.d("CONTROLS", "run: ================================== dis " + id);
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
