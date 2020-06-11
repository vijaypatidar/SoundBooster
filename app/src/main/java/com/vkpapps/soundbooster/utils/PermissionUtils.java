package com.vkpapps.soundbooster.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
/*
 * @author VIJAY PATIDAR
 * */

public class PermissionUtils {
    public static boolean checkStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void askStoragePermission(Activity activity, int code) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, code);
    }
}
