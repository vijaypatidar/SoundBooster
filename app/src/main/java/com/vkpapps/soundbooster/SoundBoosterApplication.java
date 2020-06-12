package com.vkpapps.soundbooster;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.StorageManager;
import com.vkpapps.soundbooster.utils.Utils;

public class SoundBoosterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        setDefaultUser();

        StorageManager storageManager = new StorageManager(this);
        storageManager.getAllAudioFromDevice();
        storageManager.deleteDir(storageManager.getSongDir());
    }


    private void setDefaultUser() {
        User user = Utils.loadUser(this);
        if (user == null) {
            user = new User();
            user.setName("RockStar");
            user.setUserId(String.valueOf(System.currentTimeMillis()));
            user.setAccess(true);
            Utils.setUser(user, this);
        }
    }

}
