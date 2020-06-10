package com.vkpapps.soundbooster.interfaces;

import android.media.MediaPlayer;

public interface OnMediaPlayerChangeListener {
    void onChangeSong(String title, MediaPlayer mediaPlayer);

    void onPlayingStatusChange(boolean isPlaying);

    void onVolumeChange(float volume);
}
