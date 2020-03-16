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
            Log.d("CONTROLS", "handleMessage: " + command.substring(0, 3));
            if (isHost) {
                onMessageHandlerListener.broadcastCommand(command);
            }
            switch (command.substring(0, 3)) {
                case "PLY":
                    onMessageHandlerListener.onPlayRequest(command.substring(3));
                    break;
                case "PAS":
                    onMessageHandlerListener.onPauseRequest();
                    break;
                case "SKT":
                    onMessageHandlerListener.onSeekToRequest(Integer.parseInt(command.substring(3)));
                    break;
                case "RFR":
                    onMessageHandlerListener.onReceiveFileRequest(command.substring(3), msg.getData().getString("ID"));
                    break;
                case "SFR":
                    onMessageHandlerListener.onSendFileRequest(command.substring(3), msg.getData().getString("ID"));
                    break;
                case "RFC":
                    onMessageHandlerListener.onReceiveFileRequestAccepted(command.substring(3), msg.getData().getString("ID"));
                    break;
                case "SFC":
                    onMessageHandlerListener.onSendFileRequestAccepted(command.substring(3), msg.getData().getString("ID"));
                    break;
                case "DCN":
                    onMessageHandlerListener.onNewDeviceConnected(msg.getData().getString("ID"));
                    break;
                case "DDN":
                    onMessageHandlerListener.onDeviceDisconnected(msg.getData().getString("ID"));
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

        void onNewDeviceConnected(String id);

        void onDeviceDisconnected(String id);

        void onSendFileRequest(String name, String id);

        void onReceiveFileRequest(String name, String id);

        void onSendFileRequestAccepted(String name, String id);

        void onReceiveFileRequestAccepted(String name, String id);
    }
}
