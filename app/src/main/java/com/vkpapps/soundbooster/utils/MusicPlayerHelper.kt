package com.vkpapps.soundbooster.utils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vkpapps.soundbooster.analitics.Logger
import com.vkpapps.soundbooster.model.control.ControlPlayer
import com.vkpapps.soundbooster.receivers.MediaChangeReceiver
import java.io.File
import java.io.IOException


/***
 * @author VIJAY PATIDAR
 */
class MusicPlayerHelper(private val context: Context) {

    private val mediaPlayer = MediaPlayer()
    private val root: File = StorageManager(context).songDir
    private var onMusicPlayerHelperListener: OnMusicPlayerHelperListener? = null
    private var current: String? = null


    fun loadAndPlay(name: String?) {
        if (name == null) return
        Logger.d("loadAndPlay: $name")
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(File(root, name).absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            current = name
            onMusicPlayerHelperListener?.onSongChange(name)
            sendLocalBroadcast(ControlPlayer.ACTION_PLAY, name)
        } catch (e: IOException) {
            onMusicPlayerHelperListener?.onRequestSongNotFound(name)
            e.printStackTrace()
        }
    }


    val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    private fun resume() {
        try {
            mediaPlayer.start()
            sendLocalBroadcast(ControlPlayer.ACTION_RESUME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        try {
            mediaPlayer.pause()
            sendLocalBroadcast(ControlPlayer.ACTION_PAUSE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun seekTo(dur: Int) {
        try {
            mediaPlayer.seekTo(dur)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setVolume(vol: Float) {
        try {
            mediaPlayer.setVolume(vol, vol)
        } catch (ignored: Exception) {
        }
    }

    fun handleControl(control: ControlPlayer) {
        when (control.action) {
            ControlPlayer.ACTION_PLAY -> loadAndPlay(control.data)
            ControlPlayer.ACTION_PAUSE -> pause()
            ControlPlayer.ACTION_SEEK_TO -> seekTo(control.intData)
            ControlPlayer.ACTION_CHANGE_VOLUME -> setVolume(control.intData.toFloat())
            ControlPlayer.ACTION_NEXT -> {
                val next = onMusicPlayerHelperListener?.getNextSong(1)
                loadAndPlay(next)
            }
            ControlPlayer.ACTION_PREVIOUS -> {
                val next = onMusicPlayerHelperListener?.getNextSong(-1)
                loadAndPlay(next)
            }
            ControlPlayer.ACTION_RESUME -> {
                onMusicPlayerHelperListener?.onResumePaying()
                resume()
            }
        }
    }

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    private fun sendLocalBroadcast(action: Int, data: String = "") {
        Logger.d("$action  $data")
        val intent = Intent(MediaChangeReceiver.MEDIA_PLAYER_CHANGE)
        intent.putExtra(MediaChangeReceiver.PARAM_ACTION_TYPE, action)
        intent.putExtra(MediaChangeReceiver.PARAM_ACTION_DATA, data)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    interface OnMusicPlayerHelperListener {

        fun onSongChange(name: String?)
        fun onRequestSongNotFound(songName: String?)
        fun getNextSong(change: Int): String?
        fun onResumePaying()
    }

    fun setOnMusicPlayerHelperListener(onMusicPlayerHelperListener: OnMusicPlayerHelperListener) {
        this.onMusicPlayerHelperListener = onMusicPlayerHelperListener
    }

    fun getCurrentSongName(): String? {
        return current
    }


    private val onAudioFocusChangeListenerAudioManager = AudioManager.OnAudioFocusChangeListener {
        when (it) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer.setVolume(1f, 1f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.setVolume(0f, 0f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer.setVolume(0.1f, .1f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer.setVolume(0f, 0f)
            }
        }
    }

    fun setUpFocusListener() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        am?.requestAudioFocus(onAudioFocusChangeListenerAudioManager, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
        mediaPlayer.setVolume(1f, 1f)
    }

}
