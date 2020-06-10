package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.model.control.ControlFile;
import com.vkpapps.soundbooster.model.control.ControlPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHelper extends Thread {
    private final String TAG = "ClientHelper";
    public User user;
    private ObjectOutputStream outputStream;
    private SignalHandler signalHandler;
    private Socket socket;
    private OnClientConnectionStateListener onClientConnectionStateListener;

    public ClientHelper(Socket socket, @NonNull SignalHandler signalHandler, User user, OnClientConnectionStateListener onClientConnectionStateListener) {
        this.socket = socket;
        this.signalHandler = signalHandler;
        this.user = user;
        this.onClientConnectionStateListener = onClientConnectionStateListener;
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        bundle.putString("ID", user.getUserId());
        try {
            Log.d(TAG, "run: ==================================== connecting...to istream ");
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            // send identity to connected device
            outputStream.writeObject(user);
            outputStream.flush();

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Object object = inputStream.readObject();
            Log.d(TAG, "run: ==================================== connect " + user);
            if (object instanceof User) {

                user = (User) object;

                //notify user added
                if (onClientConnectionStateListener != null) {
                    onClientConnectionStateListener.onClientConnected(this);
                }

                while (socket.isConnected()) {
                    object = inputStream.readObject();
                    if (object instanceof ControlPlayer) {
                        ControlPlayer controlPlayer = (ControlPlayer) object;
                        Message message = new Message();
                        controlPlayer.copyToBundle(bundle);
                        message.setData(bundle);
                        signalHandler.sendMessage(message);
                    } else if (object instanceof ControlFile) {
                        ControlFile controlPlayer = (ControlFile) object;
                        Message message = new Message();
                        bundle.putInt("action", controlPlayer.getAction());
                        bundle.putString("data", controlPlayer.getData());
                        message.setData(bundle);
                        signalHandler.sendMessage(message);
                    } else if (object instanceof User) {
                        User u = (User) object;
                        if (u.getUserId().equals(user.getUserId())) {
                            user.setAccess(u.isAccess());
                            user.setName(u.getName());
                        }
                    } else {
                        System.err.println("invalid " + object);
                    }
                }

            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // notify client leaved or disconnected
        Log.d(TAG, "run: ==================================== disconnect ");
        if (onClientConnectionStateListener != null) {
            onClientConnectionStateListener.onClientDisconnected(this);
        }
    }

    public void write(@NonNull Object command) {
        new Thread(() -> {
            try {
                outputStream.writeObject(command);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void writeIfClient(@NonNull Object command, String clientId) {
        new Thread(() -> {
            if (user.getUserId().equals(clientId)) {
                try {
                    outputStream.writeObject(command);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
