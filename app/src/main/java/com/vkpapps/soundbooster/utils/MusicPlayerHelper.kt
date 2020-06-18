package com.vkpapps.soundbooster.utils

import android.content.Context
import android.media.MediaPlayer
import com.vkpapps.soundbooster.analitics.Logger
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener
import com.vkpapps.soundbooster.model.control.ControlPlayer
import java.io.File
import java.io.IOException

/***
 * @author VIJAY PATIDAR
 */
class MusicPlayerHelper(context: Context?) {
    private val mediaPlayer = MediaPlayer()
    private val root: File = StorageManager(context).songDir
    private var playerChangeListener: OnMediaPlayerChangeListener? = null
    private var onMusicPlayerHelperListener: OnMusicPlayerHelperListener? = null
    private var current: String? = null

    fun setPlayerChangeListener(playerChangeListener: OnMediaPlayerChangeListener?) {
        this.playerChangeListener = playerChangeListener
        playerChangeListener?.onPlayingStatusChange(mediaPlayer.isPlaying)
        if (current != null)
            playerChangeListener?.onChangeSong(current!!, mediaPlayer)
    }

    fun setOnMusicPlayerHelperListener(onMusicPlayerHelperListener: OnMusicPlayerHelperListener) {
        this.onMusicPlayerHelperListener = onMusicPlayerHelperListener
    }


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
            playerChangeListener?.onChangeSong(name, mediaPlayer)
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
            playerChangeListener?.onPlayingStatusChange(mediaPlayer.isPlaying)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        try {
            mediaPlayer.pause()
            playerChangeListener?.onPlayingStatusChange(mediaPlayer.isPlaying)
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
            if (playerChangeListener != null) {
                playerChangeListener?.onVolumeChange(vol)
            }
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

    interface OnMusicPlayerHelperListener {
        fun onSongChange(name: String?)
        fun onRequestSongNotFound(songName: String?)
        fun getNextSong(change: Int): String?
        fun onResumePaying()
    }

}