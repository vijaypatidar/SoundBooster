package com.vkpapps.soundbooster.interfaces

import com.vkpapps.soundbooster.connection.ClientHelper

/***
 * @author VIJAY PATIDAR
 */


interface OnClientConnectionStateListener {
    fun onClientConnected(clientHelper: ClientHelper)
    fun onClientDisconnected(clientHelper: ClientHelper)
}