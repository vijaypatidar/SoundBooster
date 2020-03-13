package com.vkpapps.soundbooster.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Objects;


public class SignalHandler extends Handler {
    public static final int PLAY = 3;
    public static final int PLAY_THIS = 4;
    public static final int PAUSE = 11;
    public static final int PLAY_NEXT = 7;
    public static final int PLAY_PREVIOUS = 7;

    // for client only
    public static final int CONNECT_TO_HOST = 6;

    // for host only
    public static final int NEW_DEVICE_CONNECTED = 1;

    private final OnMessageHandlerListener onMessageHandlerListener;

    public SignalHandler(OnMessageHandlerListener onMessageHandlerListener) {
        this.onMessageHandlerListener = onMessageHandlerListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            String command = Objects.requireNonNull(msg.getData().getCharSequence("command")).toString();
            String[] commands = command.split(" ");
            switch (commands[0]) {
                case "PL":
                    onMessageHandlerListener.onPlayRequest(commands[1]);
                    break;
                case "PA":
                    onMessageHandlerListener.onPauseRequest();
                    break;
                case "ST":
                    onMessageHandlerListener.onSeekToRequest(Long.parseLong(commands[1]));
                    break;
                default:
                    Log.d("CONTROLS", "handleMessage: =================================== invalid req " + commands.toString());
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public interface OnMessageHandlerListener {
        void onPlayRequest(String name);

        void onResumeRequest();

        void onPauseRequest();

        void onSeekToRequest(long time);

        void onIdentityRequest();
    }
}
