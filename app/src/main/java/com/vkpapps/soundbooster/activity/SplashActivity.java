package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MobileAds.initialize(this);
        Utils.root = getDir("userData", MODE_PRIVATE);
        Utils.imageRoot = getDir("image", MODE_PRIVATE);

        deleteFiles(Utils.imageRoot);
        deleteFiles(getDir("song", MODE_PRIVATE));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);

    }

    private void deleteFiles(@NonNull File dir) {
        try {
            File[] entries = dir.listFiles();
            for (File s : entries) {
                s.delete();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
