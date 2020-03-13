package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.utils.PermissionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private TextView message;
    private SignalHandler signalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtils.checkStoragePermission(this)) {
            PermissionUtils.askStoragePermission(this);
        }

        intent = new Intent(this, PartyActivity.class);
        createHost();
    }


    private void setup() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : wmMethods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    boolean isWifiAPEnabled = (Boolean) method.invoke(wifiManager);
                    if (isWifiAPEnabled) {
                        createHost();
                    } else {
                        connectToHost();
                    }
                } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createHost() {
        Toast.makeText(this, "host", Toast.LENGTH_SHORT).show();
        intent.putExtra("isHost", true);
        startActivity(intent);
        finish();
    }

    private void connectToHost() {
        String host = "192.168.43.1";
            intent.putExtra("isHost", false);
            intent.putExtra("host", host);
    }
}
