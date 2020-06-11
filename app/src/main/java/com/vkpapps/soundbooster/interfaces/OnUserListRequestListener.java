package com.vkpapps.soundbooster.interfaces;

import com.vkpapps.soundbooster.connection.ClientHelper;

import java.util.List;

/*
 * @author VIJAY PATIDAR
 * */
public interface OnUserListRequestListener {
    List<ClientHelper> onRequestUsers();
}
