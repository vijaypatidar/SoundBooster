package com.vkpapps.soundbooster.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MusicPlayerHelper {
    @SuppressLint("StaticFieldLeak")
    private static MusicPlayerHelper musicPlayerHelper;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private File root;
    private OnMusicPlayerHelperListener onMusicPlayerHelperListener;

    public MusicPlayerHelper(Context context, OnMusicPlayerHelperListener onMusicPlayerHelperListener) {
        this.root = context.getDir("song", Context.MODE_PRIVATE);
        this.onMusicPlayerHelperListener = onMusicPlayerHelperListener;
    }

    public static MusicPlayerHelper getInstance(Context context, OnMusicPlayerHelperListener onMusicPlayerHelperListener) {
        if (musicPlayerHelper == null) {
            musicPlayerHelper = new MusicPlayerHelper(context, onMusicPlayerHelperListener);
        }

        return musicPlayerHelper;
    }

    public void loadAndPlay(String name) {
        try {
            Log.d("CONTROLS", "loadAndPlay:==========  >" + name + "<");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(new File(root, name).getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (onMusicPlayerHelperListener != null) {
                onMusicPlayerHelperListener.onSongChange(name);
            }
        } catch (IOException e) {
            //TODO when no such file found
            onMusicPlayerHelperListener.onRequestSongNotFound(name);
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            mediaPlayer.pause();
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

    public interface OnMusicPlayerHelperListener {
        void onSongChange(String name);

        void onRequestSongNotFound(String songName);
    }
}
