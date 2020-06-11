package com.vkpapps.soundbooster.interfaces;

import com.vkpapps.soundbooster.model.control.ControlPlayer;

public interface OnControlRequestListener {
    void onMusicPlayerControl(ControlPlayer controlPlayer);

    void onDownloadRequest(String name, String id);

    void onDownloadRequestAccepted(String name, String id);

    void onUploadRequest(String name, String id);

    void onUploadRequestAccepted(String name, String id);

}
