package com.vkpapps.soundbooster.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;

import java.io.File;
import java.io.IOException;

public class MusicPlayerHelper {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private File root;
    private OnMusicPlayerHelperListener onMusicPlayerHelperListener;
    private OnMediaPlayerChangeListener playerChangeListener;
    private String current;

    public void setPlayerChangeListener(OnMediaPlayerChangeListener playerChangeListener) {
        this.playerChangeListener = playerChangeListener;
        if (playerChangeListener != null) {
            playerChangeListener.onChangeSong(current, mediaPlayer);
        }
    }

    public MusicPlayerHelper(Context context, OnMusicPlayerHelperListener onMusicPlayerHelperListener) {
        this.root = context.getDir("song", Context.MODE_PRIVATE);
        this.onMusicPlayerHelperListener = onMusicPlayerHelperListener;
    }

    public void loadAndPlay(String name) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(new File(root, name).getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            current = name;
            if (onMusicPlayerHelperListener != null) {
                onMusicPlayerHelperListener.onSongChange(name);
            }
            if (playerChangeListener != null) {
                playerChangeListener.onChangeSong(name, mediaPlayer);
            }
        } catch (IOException e) {
            onMusicPlayerHelperListener.onRequestSongNotFound(name);
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            mediaPlayer.start();
            if (playerChangeListener != null) {
                playerChangeListener.onPlayingStatusChange(mediaPlayer.isPlaying());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            mediaPlayer.pause();
            if (playerChangeListener != null) {
                playerChangeListener.onPlayingStatusChange(mediaPlayer.isPlaying());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int dur) {
        try {
            mediaPlayer.seekTo(dur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float vol) {
        try {
            mediaPlayer.setVolume(vol, vol);
            if (playerChangeListener != null) {
                playerChangeListener.onVolumeChange(vol);
            }
        } catch (Exception ignored) {
        }
    }


    public interface OnMusicPlayerHelperListener {
        void onSongChange(String name);
        void onRequestSongNotFound(String songName);
    }
}
