package com.vkpapps.soundbooster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vkpapps.soundbooster.connection.FileService;

public class FileRequestReceiver extends BroadcastReceiver {

    private OnFileRequestReceiverListener onFileRequestReceiverListener;

    public FileRequestReceiver(OnFileRequestReceiverListener onFileRequestReceiverListener) {
        this.onFileRequestReceiverListener = onFileRequestReceiverListener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String name = intent.getStringExtra("name");
        if (action.equals(FileService.STATUS_SUCCESS)) {
            onFileRequestReceiverListener.onRequestSuccess(name);
        } else {
            onFileRequestReceiverListener.onRequestFailed(name);
        }
    }

    public interface OnFileRequestReceiverListener {
        void onRequestFailed(String name);

        void onRequestSuccess(String name);
    }

}
