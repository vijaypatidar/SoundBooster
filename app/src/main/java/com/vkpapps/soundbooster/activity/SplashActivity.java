package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.vkpapps.soundbooster.utils.Utils.root;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        root = getDir("files", MODE_PRIVATE);
        final File user = new File(root, "user");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (user.exists()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, UserDetailActivity.class));
                }
                finish();
            }
        }, 1500);

    }
}
