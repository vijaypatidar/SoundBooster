package com.vkpapps.soundbooster.interfaces

import com.vkpapps.soundbooster.connection.ClientHelper

/**
 * @author VIJAY PATIDAR
 */
interface OnUserListRequestListener {
    fun onRequestUsers(): List<ClientHelper?>?
}