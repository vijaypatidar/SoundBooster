package com.vkpapps.soundbooster.interfaces

import com.vkpapps.soundbooster.model.AudioModel

/**
 * @author VIJAY PATIDAR
 */
interface OnHostSongFragmentListener {
    fun onHostAudioSelected(name: AudioModel)
}