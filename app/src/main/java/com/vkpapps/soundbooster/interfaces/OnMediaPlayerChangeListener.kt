package com.vkpapps.soundbooster.interfaces

import android.media.MediaPlayer

/**
 * @author VIJAY PATIDAR
 */
interface OnMediaPlayerChangeListener {
    fun onChangeSong(title: String, mediaPlayer: MediaPlayer)
    fun onPlayingStatusChange(isPlaying: Boolean)
    fun onVolumeChange(volume: Float)
}