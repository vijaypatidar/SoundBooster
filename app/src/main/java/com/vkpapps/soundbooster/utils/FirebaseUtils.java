package com.vkpapps.soundbooster.utils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


/**
 * @author VIJAY PATIDAR
 * */
public class FirebaseUtils {
    public static void getAdRequest(AdView adView) {
        adView.loadAd(new AdRequest.Builder().build());
    }

    public static void getAdRequest(InterstitialAd adView) {
        adView.loadAd(new AdRequest.Builder().build());
    }
}