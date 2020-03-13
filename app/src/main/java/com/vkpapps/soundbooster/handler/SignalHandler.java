package com.vkpapps.soundbooster.handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Objects;


public class SignalHandler extends Handler {

    private final OnMessageHandlerListener onMessageHandlerListener;
    private boolean isHost;

    public SignalHandler(OnMessageHandlerListener onMessageHandlerListener, boolean isHost) {
        this.onMessageHandlerListener = onMessageHandlerListener;
        this.isHost = isHost;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            String command = Objects.requireNonNull(msg.getData().getString("command"));
            Log.d("CONTROLS", "handleMessage: " + command.substring(0, 2));
            if (isHost) {
                onMessageHandlerListener.broadcastCommand(command);
            }
            switch (command.substring(0, 2)) {
                case "PL":
                    onMessageHandlerListener.onPlayRequest(command.substring(3));
                    break;
                case "PA":
                    onMessageHandlerListener.onPauseRequest();
                    break;
                case "ST":
                    onMessageHandlerListener.onSeekToRequest(Integer.parseInt(command.substring(3)));
                    break;
                case "ID":
                    onMessageHandlerListener.onIdentityRequest(command.substring(3), msg.getData().getString("ID"));
                    break;
                default:
                    Log.d("CONTROLS", "handleMessage: =================================== invalid req " + command);
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public interface OnMessageHandlerListener {
        void onPlayRequest(String name);

        void onResumeRequest();

        void onPauseRequest();

        void onSeekToRequest(int time);

        void broadcastCommand(String command);

        void onIdentityRequest(String user, String id);
    }
}
