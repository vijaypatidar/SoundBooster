package com.vkpapps.soundbooster.interfaces;

import com.vkpapps.soundbooster.connection.ClientHelper;

import java.util.List;

public interface OnUserListRequestListener {
    List<ClientHelper> onRequestUsers();
}
