package com.vkpapps.soundbooster.interfaces

import androidx.annotation.UiThread

/**
 * @author VIJAY PATIDAR
 */
interface OnMediaPlayerChangeListener {
    @UiThread
    fun onChangeSong(title: String)

    @UiThread
    fun onPlayingStatusChange(isPlaying: Boolean)

    @UiThread
    fun onVolumeChange(volume: Float)
}