package com.vkpapps.soundbooster.utils

import android.content.Context
import android.net.wifi.WifiManager

/**
 * @author VIJAY PATIDAR
 */
class IPManager(context: Context) {
    private val manager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private fun intToIp(i: Int): String {
        return (i and 0xFF).toString() + "." + (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." + (i shr 24 and 0xFF)
    }

    fun hostIp(): String {
        return intToIp(manager.dhcpInfo.gateway)
    }
}