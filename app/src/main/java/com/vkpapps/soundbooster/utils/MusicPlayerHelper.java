package com.vkpapps.soundbooster.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;
import com.vkpapps.soundbooster.model.control.ControlPlayer;

import java.io.File;
import java.io.IOException;

public class MusicPlayerHelper {
    private static MusicPlayerHelper musicPlayerHelper;
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

    private MusicPlayerHelper(Context context, OnMusicPlayerHelperListener onMusicPlayerHelperListener) {
        this.root = new StorageManager(context).getSongDir();
        this.onMusicPlayerHelperListener = onMusicPlayerHelperListener;
    }

    public static MusicPlayerHelper getInstance(Context context, OnMusicPlayerHelperListener onMusicPlayerHelperListener) {
        if (musicPlayerHelper == null) {
            musicPlayerHelper = new MusicPlayerHelper(context, onMusicPlayerHelperListener);
        }
        musicPlayerHelper.onMusicPlayerHelperListener = onMusicPlayerHelperListener;
        return musicPlayerHelper;
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


    public void handleControl(ControlPlayer control) {
        // todo add next and previous
        switch (control.getAction()) {
            case ControlPlayer.ACTION_PLAY:
                loadAndPlay(control.getData());
                break;
            case ControlPlayer.ACTION_PAUSE:
                pause();
                break;
            case ControlPlayer.ACTION_SEEK_TO:
                seekTo(control.getIntData());
                break;
            case ControlPlayer.ACTION_CHANGE_VOLUME:
                setVolume(control.getIntData());
                break;
            case ControlPlayer.ACTION_NEXT:
                String next = onMusicPlayerHelperListener.getNextSong(1);
                if (next != null) {
                    loadAndPlay(next);
                }
                break;
            case ControlPlayer.ACTION_PREVIOUS:
                next = onMusicPlayerHelperListener.getNextSong(-1);
                if (next != null) {
                    loadAndPlay(next);
                }
                break;
        }
    }


    public interface OnMusicPlayerHelperListener {
        void onSongChange(String name);

        void onRequestSongNotFound(String songName);

        String getNextSong(int change);
    }

}
