package com.vkpapps.soundbooster.handler;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class FileHandler extends Handler {

    public final static int REQUEST_COMPLETED = 2;
    public final static int REQUEST_FAILED = 3;
    private final OnFileHandlerListener onFileHandlerListener;

    public FileHandler(OnFileHandlerListener onFileHandlerListener) {
        this.onFileHandlerListener = onFileHandlerListener;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        int what = msg.what;
        switch (what) {
            case REQUEST_COMPLETED:
                onFileHandlerListener.onFileRequestCompleted();
                break;
            case REQUEST_FAILED:
                onFileHandlerListener.onFileRequestFailed();
                break;

        }
    }

    public interface OnFileHandlerListener {
        void onFileRequestCompleted();

        void onFileRequestFailed();
    }
}
