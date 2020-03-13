package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.handler.SignalHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommandHelperRunnable implements Runnable {
    private OutputStream outputStream;
    private SignalHandler signalHandler;
    private Socket socket;

    public CommandHelperRunnable(Socket socket, @NonNull SignalHandler signalHandler) {
        this.socket = socket;
        this.signalHandler = signalHandler;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection established");
            InputStream inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            byte[] bytes = new byte[2048];
            String command;
            while (true) {
                int read = inputStream.read(bytes);
                command = new String(bytes, 0, read);
                if (command.equals("exit")) break;
                System.out.println(command);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("command", command);
                message.setData(bundle);
                signalHandler.sendMessage(message);
            }
            //TODO notify connection closed
            outputStream.close();
            inputStream.close();
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
}
