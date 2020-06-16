package com.vkpapps.soundbooster

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.vkpapps.soundbooster.model.User
import com.vkpapps.soundbooster.utils.StorageManager
import com.vkpapps.soundbooster.utils.UserUtils

/**
 * @author VIJAY PATIDAR
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        user = UserUtils(this).loadUser()
        val storageManager = StorageManager(this)
        storageManager.allAudioFromDevice
        storageManager.deleteDir(storageManager.songDir)
    }

    companion object {
        @JvmStatic
        lateinit var user: User
    }
}