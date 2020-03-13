package com.vkpapps.soundbooster.utils;

import com.google.android.gms.ads.AdRequest;


public class FirebaseUtils {
    public static AdRequest getAdRequest() {
        return new AdRequest.Builder().addTestDevice("C2703F91A6A15DEF3D7421C20510CA2D").build();
//        return new AdRequest.Builder().build();
    }
}