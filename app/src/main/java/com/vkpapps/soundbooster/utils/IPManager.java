package com.vkpapps.soundbooster.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class IPManager {
    private WifiManager manager;

    public IPManager(Context context) {
        this.manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }

    public String hostIp() {
        DhcpInfo dhcpInfo = manager.getDhcpInfo();
        return intToIp(dhcpInfo.gateway);
    }


}
