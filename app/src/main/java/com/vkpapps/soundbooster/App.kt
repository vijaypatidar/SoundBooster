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
        setDefaultUser()
        val storageManager = StorageManager(this)
        storageManager.allAudioFromDevice
        storageManager.deleteDir(storageManager.songDir)
    }

    private fun setDefaultUser() {
        user = UserUtils(this).loadUser()
        if (user == null) {
            user = User()
            user!!.name = "RockStar"
            user!!.userId = System.currentTimeMillis().toString()
            UserUtils(this).setUser(user)
        }
    }

    companion object {
        @JvmStatic
        var user: User? = null

    }
}