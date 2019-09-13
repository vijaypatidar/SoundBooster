package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.WifiHelper;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.NewSongModel;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.SeekModel;
import com.vkpapps.soundbooster.view.CircleView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements CircleView.OnCircleViewListener, SignalHandler.OnMessageHandlerListener {
    private Intent intent;
    private TextView message;
    private SignalHandler signalHandler;
    private ClientHelper clientHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signalHandler = new SignalHandler(this);
        intent = new Intent(MainActivity.this, PartyActivity.class);
        CircleView circleView = findViewById(R.id.circleView);
        circleView.setOnCircleViewListener(this);

        WifiHelper.getDeviceList();
        message = findViewById(R.id.message);
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
        intent.putExtra("isHost", true);
        startActivity(intent);
    }

    private void connectToHost() {

        if (WifiHelper.deviceList.size() > 0) {
            String host = WifiHelper.deviceList.get(0);
            Toast.makeText(MainActivity.this, host, Toast.LENGTH_SHORT).show();
            intent.putExtra("isHost", false);
            intent.putExtra("host", host);
            clientHelper = new ClientHelper(host, signalHandler);
            clientHelper.start();
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
    public void handleMessage(int what, String s) {
    }

    @Override
    public void handleConnectToHost() {
        startActivity(intent);
        clientHelper.stopClientHelper();

    }

    @Override
    public void handleSeek(SeekModel seekModel) {

    }

    @Override
    public void handelNewSong(NewSongModel newSongModel) {

    }

    @Override
    public void handleControl(Control control) {

    }

    @Override
    public void handleSongPlay(PlayThisSong playThisSong) {

    }
}
