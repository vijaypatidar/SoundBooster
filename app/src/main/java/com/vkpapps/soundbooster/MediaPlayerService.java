package com.vkpapps.soundbooster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.widget.ImageView;

import com.vkpapps.soundbooster.model.Control;

import java.io.IOException;

public class MediaPlayerService {
    private static MediaPlayerService ourInstance = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String root;

    private MediaPlayerService(String root) {
        this.root = root;
    }

    public static MediaPlayerService getInstance(String root) {
        if (ourInstance == null) {
            ourInstance = new MediaPlayerService(root);
        }
        return ourInstance;
    }

    private void load(String index) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(index);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void seek(final int seekTo, long date) {
        mediaPlayer.seekTo(seekTo);
    }


    public int calculateSeek(int percentage) {
        int total = mediaPlayer.getDuration();
        return total * percentage / 100;
    }

    public void loadPic(String path, ImageView songPic) {
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);

            byte[] data = mmr.getEmbeddedPicture();

            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                songPic.setImageBitmap(bitmap); //associated cover art in bitmap
            }
        } catch (Exception ignored) {

        }

    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void processControlRequest(Control control) {
        switch (control.getChoice()) {
            case Control.PLAY:
                if (control.getName() != null) load(root + control.getName());
                mediaPlayer.start();
                break;
            case Control.PAUSE:
                mediaPlayer.pause();
                break;
            case Control.SEEK:
                mediaPlayer.seekTo(control.getValue());
                break;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
