package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnControlRequestListener;
import com.vkpapps.soundbooster.interfaces.OnObjectReceiveListener;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.model.control.ControlFile;
import com.vkpapps.soundbooster.model.control.ControlPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHelper extends Thread {
    public User user;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private OnControlRequestListener onControlRequestListener;
    private OnClientConnectionStateListener onClientConnectionStateListener;
    private OnObjectReceiveListener onObjectReceiveListener;

    public ClientHelper(Socket socket, OnControlRequestListener onControlRequestListener, User user, OnClientConnectionStateListener onClientConnectionStateListener) {
        this.socket = socket;
        this.user = user;
        this.onControlRequestListener = onControlRequestListener;
        this.onClientConnectionStateListener = onClientConnectionStateListener;
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        bundle.putString("ID", user.getUserId());
        String TAG = "ClientHelper";
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
                        handleControl(controlPlayer);
                    } else if (object instanceof ControlFile) {
                        ControlFile control = (ControlFile) object;
                        handleFileControl(control);
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

    private void handleControl(ControlPlayer control) {

        if (onObjectReceiveListener != null) {
            onObjectReceiveListener.onObjectReceive(control);
        }
        Log.d("CONTROLS", "handleMessage: =================================== req " + control.getAction());
        onControlRequestListener.onMusicPlayerControl(control);
    }

    private void handleFileControl(ControlFile control) {
        try {
            Log.d("CONTROLS", "handleMessage: =================================== req " + control.getAction());
            switch (control.getAction()) {
                case ControlFile.DOWNLOAD_REQUEST:
                    onControlRequestListener.onDownloadRequest(control.getData(), control.getId());
                    break;
                case ControlFile.UPLOAD_REQUEST:
                    onControlRequestListener.onUploadRequest(control.getData(), control.getId());
                    break;
                case ControlFile.DOWNLOAD_REQUEST_CONFIRM:
                    onControlRequestListener.onDownloadRequestAccepted(control.getData(), control.getId());
                    break;
                case ControlFile.UPLOAD_REQUEST_CONFIRM:
                    onControlRequestListener.onUploadRequestAccepted(control.getData(), control.getId());
                    break;
                default:
                    Log.d("CONTROLS", "handleMessage: =================================== invalid req ");
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setOnObjectReceiveListener(OnObjectReceiveListener onObjectReceiveListener) {
        this.onObjectReceiveListener = onObjectReceiveListener;
    }
}
