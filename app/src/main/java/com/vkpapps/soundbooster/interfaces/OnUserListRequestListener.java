package com.vkpapps.soundbooster.interfaces;

import com.vkpapps.soundbooster.model.User;

import java.util.List;

public interface OnUserListRequestListener {
    List<User> onRequestUsers();
}
