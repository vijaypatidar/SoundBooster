package com.vkpapps.soundbooster.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vkpapps.soundbooster.analitics.Logger
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener
import com.vkpapps.soundbooster.model.control.ControlPlayer

class MediaChangeReceiver : BroadcastReceiver(), OnMediaPlayerChangeListener {
    private val onMediaPlayerChangeListeners = ArrayList<OnMediaPlayerChangeListener>()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra(PARAM_ACTION_TYPE, -1)) {
            ControlPlayer.ACTION_PAUSE -> onPlayingStatusChange(false)
            ControlPlayer.ACTION_RESUME -> onPlayingStatusChange(true)
            ControlPlayer.ACTION_PLAY -> {
                val data = intent.getStringExtra(PARAM_ACTION_DATA)
                if (!data.isNullOrEmpty()) onChangeSong(data)
            }
        }
    }


    fun addOnMediaPlayerChangeListener(onMediaPlayerChangeListener: OnMediaPlayerChangeListener) {
        onMediaPlayerChangeListeners.add(onMediaPlayerChangeListener)
    }

    fun removeOnMediaPlayerChangeListener(onMediaPlayerChangeListener: OnMediaPlayerChangeListener) {
        onMediaPlayerChangeListeners.remove(onMediaPlayerChangeListener)
    }

    companion object {
        const val MEDIA_PLAYER_CHANGE = "MEDIA_PLAYER_CHANGE"
        const val PARAM_ACTION_TYPE = "ACTION_TYPE"
        const val PARAM_ACTION_DATA = "ACTION_DATA"
    }

    override fun onChangeSong(title: String) {
        Logger.d(title)
        var i = 0
        while (i < onMediaPlayerChangeListeners.size) {
            onMediaPlayerChangeListeners[i].onChangeSong(title)
            i++
        }
    }

    override fun onPlayingStatusChange(isPlaying: Boolean) {
        var i = 0
        while (i < onMediaPlayerChangeListeners.size) {
            onMediaPlayerChangeListeners[i].onPlayingStatusChange(isPlaying)
            i++
        }
    }

    override fun onVolumeChange(volume: Float) {
        var i = 0
        while (i < onMediaPlayerChangeListeners.size) {
            onMediaPlayerChangeListeners[i].onVolumeChange(volume)
            i++
        }
    }
}