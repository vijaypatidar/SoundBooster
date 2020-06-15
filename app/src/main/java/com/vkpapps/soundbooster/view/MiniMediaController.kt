package com.vkpapps.soundbooster.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.vkpapps.soundbooster.R
import com.vkpapps.soundbooster.utils.StorageManager
import java.io.File

/**
 * @author VIJAY PATIDAR
 */
class MiniMediaController : FrameLayout {
    private var audioCover: ImageView? = null
    private var audioTitle: TextView? = null
    private var btnPlay: ImageButton? = null
    private var enableVisibilityChanges = true
    private var imageRoot: File? = null
    fun setEnableVisibilityChanges(enableVisibilityChanges: Boolean) {
        this.enableVisibilityChanges = enableVisibilityChanges
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context?) : super(context!!) {
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

    fun changeSong(name: String) {
        if (enableVisibilityChanges) {
            visibility = View.VISIBLE
        }
        audioTitle!!.text = name
        val file = File(imageRoot, name)
        if (file.exists()) {
            Picasso.get().load(Uri.fromFile(file)).into(audioCover)
        }
    }

    fun setButtonOnClick(click: OnClickListener?) {
        btnPlay!!.setOnClickListener(click)
    }

    fun changePlayButtonIcon(isPlaying: Boolean) {
        btnPlay!!.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }
}