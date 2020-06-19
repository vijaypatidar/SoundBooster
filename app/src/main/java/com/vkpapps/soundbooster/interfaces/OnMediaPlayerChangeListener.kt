package com.vkpapps.soundbooster.interfaces

/**
 * @author VIJAY PATIDAR
 */
interface OnMediaPlayerChangeListener {
    fun onChangeSong(title: String)
    fun onPlayingStatusChange(isPlaying: Boolean)
    fun onVolumeChange(volume: Float)
}