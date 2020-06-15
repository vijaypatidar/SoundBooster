package com.vkpapps.soundbooster;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.StorageManager;
import com.vkpapps.soundbooster.utils.Utils;

/**
 * @author VIJAY PATIDAR
 */
public class App extends Application {
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        setDefaultUser();

        StorageManager storageManager = new StorageManager(this);
        storageManager.getAllAudioFromDevice();
        storageManager.deleteDir(storageManager.getSongDir());
    }

    public static User getUser() {
        return user;
    }

    private void setDefaultUser() {
        user = Utils.loadUser(this);
        if (user == null) {
            user = new User();
            user.setName("RockStar");
            user.setUserId(String.valueOf(System.currentTimeMillis()));
            Utils.setUser(user, this);
        }
    }

}
