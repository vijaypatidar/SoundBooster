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
    fun checkStorageReadPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun askStorageReadPermission(activity: Activity, code: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
        ), code)
    }

    @JvmStatic
    fun checkStorageWritePermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun askStorageWritePermission(activity: Activity, code: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), code)
    }
}