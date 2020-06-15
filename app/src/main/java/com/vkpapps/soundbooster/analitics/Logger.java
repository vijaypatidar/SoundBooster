package com.vkpapps.soundbooster.analitics;

import android.util.Log;

/***
 * <h1>Logger</h1>
 * <P>Utility class for analytic and debugging</P>
 * @author VIJAY PATIDAR
 * @since 2020-06-11
 * @version 1.0
 */
public class Logger {
    public static boolean logger = true;
    private static String TAG = "SoundBoosterLogger";

    /***
     * @param message text to be logged as debug on logcat
     */
    public static void d(String message) {
        if (logger)
            Log.d(TAG, message);
    }

    /***
     * @param message text to be logged as information on logcat
     * @code vijay patidar
     */
    public static void i(String message) {
        if (logger)
            Log.i(TAG, message);
    }

    /***
     * @param message text to be logged as error on logcat
     */
    public static void e(String message) {
        if (logger)
            Log.e(TAG, message);
    }
}
