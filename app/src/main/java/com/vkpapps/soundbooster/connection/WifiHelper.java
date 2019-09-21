package com.vkpapps.soundbooster.connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiHelper {
    public static final ArrayList<String> deviceList = new ArrayList<>();

    public static void getDeviceList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }
                        String[] splitted = line.split(" +");
                        if (splitted.length >= 4) {
                            String ipAddress = splitted[0];
                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);
                            Log.d("ip", "getDeviceList: " + ipAddress);
                            if (isReachable) {
                                deviceList.add(ipAddress);
                            }
                        }
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
