package com.vkpapps.soundbooster.utils;

import com.google.android.gms.ads.AdRequest;


public class FirebaseUtils {
    public static AdRequest getAdRequest() {
//        return new AdRequest.Builder().addTestDevice("A3D3C06CAE345382164A7ED2ADAAD374").build();
        return new AdRequest.Builder().build();
    }
}