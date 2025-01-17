package com.vkpapps.soundbooster.ui.view

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vkpapps.soundbooster.App
import com.vkpapps.soundbooster.R
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener
import com.vkpapps.soundbooster.utils.StorageManager
import java.io.File

/**
 * @author VIJAY PATIDAR
 */
class MiniMediaController : FrameLayout, OnMediaPlayerChangeListener {
    private var audioCover: ImageView? = null
    private var audioTitle: TextView? = null
    private var btnPlay: ImageButton? = null
    private var enableVisibilityChanges = true
    private var imageRoot: File? = null
    private var mediaPlayer: MediaPlayer = App.musicPlayerHelper.getMediaPlayer()

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        // this is passed as root param ,so that view is attached to FrameLayout i.e MiniMediaController
        val inflate = View.inflate(context, R.layout.mini_controller, this)
        audioCover = inflate.findViewById(R.id.audioCover)
        audioTitle = inflate.findViewById(R.id.audioTitle)
        btnPlay = inflate.findViewById(R.id.btnPlay)
        imageRoot = StorageManager(context).imageDir
    }

    fun setButtonOnClick(click: OnClickListener?) {
        btnPlay?.setOnClickListener(click)
    }

    private fun changePlayButtonIcon(isPlaying: Boolean) {
        btnPlay?.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun onChangeSong(title: String) {
        audioTitle?.text = title
        val file = File(imageRoot, title)
        if (file.exists()) {
            Picasso.get().load(Uri.fromFile(file)).into(audioCover)
        } else {
            audioCover?.setImageResource(R.drawable.ic_default_audio_icon)
        }
        if (enableVisibilityChanges) {
            visibility = View.VISIBLE
            animation = AnimationUtils.loadAnimation(context, R.anim.show_bottom_nav_bar)
        }
        changePlayButtonIcon(mediaPlayer.isPlaying)
    }

    override fun onPlayingStatusChange(isPlaying: Boolean) {
        changePlayButtonIcon(isPlaying)
    }

    override fun onVolumeChange(volume: Float) {

    }

    fun setEnableVisibilityChanges(enableVisibilityChanges: Boolean) {
        this.enableVisibilityChanges = enableVisibilityChanges
    }

    override fun setVisibility(visibility: Int) {
        if (enableVisibilityChanges && !audioTitle?.text.isNullOrEmpty()) super.setVisibility(visibility)
    }
}
