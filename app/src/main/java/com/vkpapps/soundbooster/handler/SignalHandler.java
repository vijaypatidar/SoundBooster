package com.vkpapps.soundbooster.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.model.Reaction;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.User;


public class SignalHandler extends Handler {
    public static final int NEW_DEVICE_CONNECTED = 1;
    public static final int NEW_SONG_REQUEST = 3;
    public static final int NEW_CONTROL_REQUEST = 4;
    public static final int HANDLE_REACTION = 11;
    public static final int CONNECT_TO_HOST = 6;
    public static final int HANDLE_REQUEST = 7;

    private final OnMessageHandlerListener onMessageHandlerListener;

    public SignalHandler(OnMessageHandlerListener onMessageHandlerListener) {
        this.onMessageHandlerListener = onMessageHandlerListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        int what = msg.what;
        Bundle bundle = msg.getData();
        switch (what) {
            case CONNECT_TO_HOST:
                onMessageHandlerListener.handleConnectToHost();
                break;
            case NEW_DEVICE_CONNECTED:
                onMessageHandlerListener.handleNewClient((User) bundle.get("data"));
                break;
            case NEW_SONG_REQUEST:
                onMessageHandlerListener.handelClientFileRequest((Request) bundle.getSerializable("data"));
                break;
            case NEW_CONTROL_REQUEST:
                onMessageHandlerListener.handleControl((Control) bundle.getSerializable("data"));
                break;
            case HANDLE_REQUEST:
                onMessageHandlerListener.handleRequest((InformClient) bundle.getSerializable("data"));
                break;
            case HANDLE_REACTION:
                onMessageHandlerListener.handleReaction((Reaction) bundle.getSerializable("data"));
        }
    }

    public interface OnMessageHandlerListener {
        void handleNewClient(User user);

        void handleConnectToHost();

        void handelClientFileRequest(Request request);

        void handleControl(Control control);

        void handleRequest(InformClient informClient);

        void handleReaction(Reaction data);

//        void handleConnectionLost();
    }
}
