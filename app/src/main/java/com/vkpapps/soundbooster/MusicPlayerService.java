package com.vkpapps.soundbooster;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vkpapps.soundbooster.model.Control;

import java.io.File;
import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final MediaPlayer MEDIA_PLAYER = new MediaPlayer();
    private final IBinder musicBind = new MusicPlayerService.MusicBinder();
    private final String TAG = "vijay";
    private String title;

    @Override
    public void onCreate() {
        super.onCreate();
        MEDIA_PLAYER.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        MEDIA_PLAYER.setAudioStreamType(AudioManager.STREAM_MUSIC);
        MEDIA_PLAYER.setOnPreparedListener(this);
        MEDIA_PLAYER.setOnCompletionListener(this);
        MEDIA_PLAYER.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MEDIA_PLAYER.stop();
        MEDIA_PLAYER.release();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MEDIA_PLAYER.stop();
        MEDIA_PLAYER.reset();
        MEDIA_PLAYER.release();
        Log.d(TAG, "onDestroy: ============================================================================= ");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }


    public void processControlRequest(Control control) {
        Log.d(TAG, "processControlRequest:  ================================ control " + control.toString());
        switch (control.getChoice()) {
            case Control.SEEK:
                MEDIA_PLAYER.seekTo(control.getValue());
                break;
            case Control.PAUSE:
                MEDIA_PLAYER.pause();
                break;
            case Control.PLAY:
                if (control.getName() == null) {
                    if (MEDIA_PLAYER.isPlaying()) {
                        MEDIA_PLAYER.pause();
                    } else {
                        MEDIA_PLAYER.start();
                    }
                } else {
                    play(control.getName());
                }
                break;
        }
    }

    public int getCalculatedSeek(int per) {
        return MEDIA_PLAYER.getDuration() * per / 100;
    }

    public int getCurrentPosition() {
        return MEDIA_PLAYER.getCurrentPosition();
    }

    public void moveBy(int i) {
    }

    private void play(String path) {
        MEDIA_PLAYER.reset();
        try {
            MEDIA_PLAYER.setDataSource(path);
            MEDIA_PLAYER.prepare();
            MEDIA_PLAYER.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        title = path;
    }

    public String getCurrentTitle() {
        return new File(title).getName();
    }

    public Bitmap getBitmapOfCurrentSong() {
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(title);
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null) {
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }

    }
}
