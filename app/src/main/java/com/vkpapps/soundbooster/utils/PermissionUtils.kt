package com.vkpapps.soundbooster.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * @author VIJAY PATIDAR
 */
object PermissionUtils {
    @JvmStatic
    fun checkStoragePermission(context: Context?): Boolean {
        return ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun askStoragePermission(activity: Activity?, code: Int) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
        ), code)
    }
}