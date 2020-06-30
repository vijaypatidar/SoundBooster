package com.vkpapps.soundbooster

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.vkpapps.soundbooster.analitics.Logger
import com.vkpapps.soundbooster.model.User
import com.vkpapps.soundbooster.utils.MusicPlayerHelper
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
        musicPlayerHelper = MusicPlayerHelper(this)
        musicPlayerHelper.setUpFocusListener()
        val storageManager = StorageManager(this)
        Thread {
            storageManager.allAudioFromDevice
        }.start()
        storageManager.deleteDir(storageManager.songDir)
        Logger.logger = BuildConfig.DEBUG
    }

    companion object {
        @JvmStatic
        lateinit var user: User

        @JvmStatic
        lateinit var musicPlayerHelper: MusicPlayerHelper
    }
}