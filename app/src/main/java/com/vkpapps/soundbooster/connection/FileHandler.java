package com.vkpapps.soundbooster.connection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.model.NewSongModel;
import com.vkpapps.soundbooster.model.SeekModel;

public class FileHandler extends Handler {
    public static final int NEW_DEVICE_CONNECTED = 1;
    public static final int NEW_SEEK_REQUEST = 2;
    public static final int NEW_SONG_REQUEST = 3;

    private OnMessageHandlerListener onMessageHandlerListener;

    public FileHandler(OnMessageHandlerListener onMessageHandlerListener) {
        this.onMessageHandlerListener = onMessageHandlerListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        int what = msg.what;
        Bundle bundle = msg.getData();
        switch (what) {
            case NEW_DEVICE_CONNECTED:
                onMessageHandlerListener.handleMessage(what, (String) bundle.get("data"));
                break;
            case NEW_SEEK_REQUEST:
                onMessageHandlerListener.handleSeek((SeekModel) bundle.get("data"));
                break;
            case NEW_SONG_REQUEST:
                onMessageHandlerListener.handelNewSong((NewSongModel) bundle.get("data"));
                break;
        }
    }

    public interface OnMessageHandlerListener {

        void handleMessage(int what, String s);

        void handleSeek(SeekModel seekModel);

        void handelNewSong(NewSongModel newSongModel);
    }
}
