package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.WifiHelper;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.PermissionUtils;
import com.vkpapps.soundbooster.utils.Utils;
import com.vkpapps.soundbooster.view.CircleView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity implements CircleView.OnCircleViewListener, SignalHandler.OnMessageHandlerListener {
    private Intent intent;
    private TextView message;
    private SignalHandler signalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signalHandler = new SignalHandler(this);
        intent = new Intent(MainActivity.this, PartyActivity.class);
        CircleView circleView = findViewById(R.id.circleView);
        circleView.setOnCircleViewListener(this);
        Utils.deleteFile(getDir("mySong", MODE_PRIVATE).getPath());
        WifiHelper.getDeviceList();
        message = findViewById(R.id.message);

        if (!PermissionUtils.checkStoragePermission(this)) {
            PermissionUtils.askStoragePermission(this);
        }
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

        if (WifiHelper.deviceList.size() > 0) {
            String host = WifiHelper.deviceList.get(0);
            intent.putExtra("isHost", false);
            intent.putExtra("host", host);
        } else {
            message.setText(getString(R.string.message_connect_host));
        }
    }

    @Override
    public void onRoundComplete() {
        WifiHelper.getDeviceList();
        setup();
    }

    @Override
    public void handleNewClient(User user) {

    }


    @Override
    public void handleConnectToHost() {
        startActivity(intent);
        finish();
    }

    @Override
    public void handleControl(Control control) {

    }


}
