package com.vkpapps.soundbooster.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.NewSongModel;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.SeekModel;
import com.vkpapps.soundbooster.model.User;


public class SignalHandler extends Handler {
    public static final int NEW_DEVICE_CONNECTED = 1;
    public static final int NEW_SEEK_REQUEST = 2;
    public static final int NEW_SONG_REQUEST = 3;
    public static final int NEW_CONTROL_REQUEST = 4;
    public static final int SONG_PLAY_REQUEST = 5;
    public static final int CONNECT_TO_HOST = 6;

    private OnMessageHandlerListener onMessageHandlerListener;

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
            case NEW_SEEK_REQUEST:
                onMessageHandlerListener.handleSeek((SeekModel) bundle.getSerializable("data"));
                break;
            case NEW_SONG_REQUEST:
                onMessageHandlerListener.handelNewSong((NewSongModel) bundle.getSerializable("data"));
                break;
            case NEW_CONTROL_REQUEST:
                onMessageHandlerListener.handleControl((Control) bundle.getSerializable("data"));
                break;
            case SONG_PLAY_REQUEST:
                onMessageHandlerListener.handleSongPlay((PlayThisSong) bundle.getSerializable("data"));
        }
    }

    public interface OnMessageHandlerListener {
        void handleNewClient(User user);

        void handleConnectToHost();

        void handleSeek(SeekModel seekModel);

        void handelNewSong(NewSongModel newSongModel);

        void handleControl(Control control);

        void handleSongPlay(PlayThisSong playThisSong);
    }
}
