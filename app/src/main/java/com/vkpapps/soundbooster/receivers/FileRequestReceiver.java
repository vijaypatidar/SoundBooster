package com.vkpapps.soundbooster.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vkpapps.soundbooster.service.FileService;

import static com.vkpapps.soundbooster.service.FileService.LAST_REQUEST;

/*
 * @author VIJAY PATIDAR
 * */
public class FileRequestReceiver extends BroadcastReceiver {

    private OnFileRequestReceiverListener onFileRequestReceiverListener;

    public FileRequestReceiver(OnFileRequestReceiverListener onFileRequestReceiverListener) {
        this.onFileRequestReceiverListener = onFileRequestReceiverListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String name = intent.getStringExtra(FileService.NAME);
        if (action == null) return;
        switch (action) {
            case FileService.STATUS_SUCCESS:
                onFileRequestReceiverListener.onRequestSuccess(name, intent.getBooleanExtra(LAST_REQUEST, false));
                break;
            case FileService.STATUS_FAILED:
                onFileRequestReceiverListener.onRequestFailed(name);
                break;
            case FileService.REQUEST_ACCEPTED:
                String clientId = intent.getStringExtra(FileService.CLIENT_ID);
                boolean send = intent.getBooleanExtra(FileService.ACTION_SEND, false);
                onFileRequestReceiverListener.onRequestAccepted(name, send, clientId);
                break;
        }
    }

    public interface OnFileRequestReceiverListener {
        void onRequestFailed(String name);

        void onRequestAccepted(String name, boolean send, String clientId);

        void onRequestSuccess(String name, boolean isLast);
    }

}
