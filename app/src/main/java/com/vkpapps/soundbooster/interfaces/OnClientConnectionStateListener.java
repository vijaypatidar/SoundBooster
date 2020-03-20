package com.vkpapps.soundbooster.interfaces;

import com.vkpapps.soundbooster.connection.ClientHelper;

public interface OnClientConnectionStateListener {
    void onClientConnected(ClientHelper clientHelper);

    void onClientDisconnected(ClientHelper clientHelper);
}
