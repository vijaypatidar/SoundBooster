package com.vkpapps.soundbooster.analitics

import android.util.Log

/***
 * <h1>Logger</h1>
 * <P>Utility class for analytic and debugging</P>
 * @author VIJAY PATIDAR
 * @since 2020-06-11
 * @version 1.0
 */
object Logger {
    var logger = true
    private const val TAG = "SoundBoosterLogger"

    /***
     * @param message text to be logged as debug on logcat
     */
    @JvmStatic
    fun d(message: String) {
        if (logger) Log.d(TAG, message)
    }

    /***
     * @param message text to be logged as information on logcat
     * @code vijay patidar
     */
    @JvmStatic
    fun i(message: String) {
        if (logger) Log.i(TAG, message)
    }

    /***
     * @param message text to be logged as error on logcat
     */
    @JvmStatic
    fun e(message: String) {
        if (logger) Log.e(TAG, message)
    }
}