package com.vkpapps.soundbooster.utils

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.vkpapps.soundbooster.BuildConfig

/**
 * @author VIJAY PATIDAR
 */
object AdsUtils {
    fun getAdRequest(adView: AdView?) {
        if (BuildConfig.DEBUG) {
            adView!!.loadAd(AdRequest.Builder().addTestDevice("1FB5455B3DFB99F776E444EB03250A40").build())
        } else {
            adView!!.loadAd(AdRequest.Builder().build())
        }
    }

    fun getAdRequest(adView: InterstitialAd?) {
        if (BuildConfig.DEBUG) {
            adView!!.loadAd(AdRequest.Builder().addTestDevice("1FB5455B3DFB99F776E444EB03250A40").build())
        } else {
            adView!!.loadAd(AdRequest.Builder().build())
        }
    }
}