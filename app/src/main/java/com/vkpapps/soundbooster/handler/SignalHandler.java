package com.vkpapps.soundbooster.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vkpapps.soundbooster.model.control.ControlFile;
import com.vkpapps.soundbooster.model.control.ControlPlayer;


public class SignalHandler extends Handler {

    private final OnMessageHandlerListener onMessageHandlerListener;

    public SignalHandler(OnMessageHandlerListener onMessageHandlerListener) {
        this.onMessageHandlerListener = onMessageHandlerListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        try {
            Bundle data = msg.getData();
            Log.d("CONTROLS", "handleMessage: =================================== req " + data.getInt("action"));
            switch (data.getInt("action")) {
                case ControlPlayer.ACTION_PLAY:
                    onMessageHandlerListener.onPlayRequest(data.getString("data"));
                    break;
                case ControlPlayer.ACTION_PAUSE:
                    onMessageHandlerListener.onPauseRequest();
                    break;
                case ControlPlayer.ACTION_SEEK_TO:
                    onMessageHandlerListener.onSeekToRequest(data.getInt("intData"));
                    break;
                case ControlPlayer.ACTION_NEXT:
                    onMessageHandlerListener.onMoveToRequest(+1);
                    break;
                case ControlPlayer.ACTION_PREVIOUS:
                    onMessageHandlerListener.onMoveToRequest(-1);
                    break;
                case ControlFile.ACTION_RECEIVE_REQUEST:
                    onMessageHandlerListener.onReceiveFileRequest(data.getString("data"), data.getString("ID"));
                    break;
                case ControlFile.ACTION_SEND_REQUEST:
                    onMessageHandlerListener.onSendFileRequest(data.getString("data"), data.getString("ID"));
                    break;
                case ControlFile.ACTION_RECEIVE_CONFIRM:
                    onMessageHandlerListener.onReceiveFileRequestAccepted(data.getString("data"), data.getString("ID"));
                    break;
                case ControlFile.ACTION_SEND_CONFIRM:
                    onMessageHandlerListener.onSendFileRequestAccepted(data.getString("data"), data.getString("ID"));
                    break;
                case ControlPlayer.ACTION_CHANGE_VOLUME:
                    onMessageHandlerListener.onVolumeChange(data.getInt("intData"));
                    break;
                default:
                    Log.d("CONTROLS", "handleMessage: =================================== invalid req ");
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

        void onSendFileRequest(String name, String id);

        void onReceiveFileRequest(String name, String id);

        void onSendFileRequestAccepted(String name, String id);

        void onVolumeChange(float vol);

        void onReceiveFileRequestAccepted(String name, String id);

        void onMoveToRequest(int parseInt);
    }
}
