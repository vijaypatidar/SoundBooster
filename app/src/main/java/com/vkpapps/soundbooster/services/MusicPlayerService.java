package com.vkpapps.soundbooster.services;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vkpapps.soundbooster.model.Control;

import java.io.File;
import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    //actions
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS";

    private final Intent actionIntent = new Intent();
    private static final MediaPlayer MEDIA_PLAYER = new MediaPlayer();

    private final IBinder musicBind = new MusicPlayerService.MusicBinder();
    private final String TAG = "vijay";
    private String title;
    private boolean run = true;
    private final Intent progressIntent = new Intent(ACTION_UPDATE_PROGRESS);
    private LocalBroadcastManager localBroadcastManager;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (run) {
                try {
                    long totalDuration = MEDIA_PLAYER.getDuration();
                    long currentDuration = MEDIA_PLAYER.getCurrentPosition();
                    int per = (int) (currentDuration * 100 / totalDuration);
                    progressIntent.putExtra("progress", per);
                    localBroadcastManager.sendBroadcast(progressIntent);
                } catch (Exception ignored) {
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    private String root;

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
        run = false;
        super.onDestroy();
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

    @Override
    public void onCreate() {
        super.onCreate();
        MEDIA_PLAYER.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        MEDIA_PLAYER.setAudioStreamType(AudioManager.STREAM_MUSIC);
        MEDIA_PLAYER.setOnPreparedListener(this);
        MEDIA_PLAYER.setOnCompletionListener(this);
        MEDIA_PLAYER.setOnErrorListener(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        root = getDir("mySong", MODE_PRIVATE).getPath() + File.separator;
        thread.start();
    }

    public void processControlRequest(Control control) {
        Log.d(TAG, "processControlRequest:  ================================ control " + control.toString());
        switch (control.getChoice()) {
            case Control.SEEK:
                seekTo(control);
                break;
            case Control.PAUSE:
                pause();
                break;
            case Control.PLAY:
                start(control);
                break;
        }
    }

    public int getCalculatedSeek(int per) {
        return MEDIA_PLAYER.getDuration() * per / 100;
    }


    public String getCurrentTitle() {
        File file = new File(title);
        if (file.exists()) {
            return file.getName();
        } else {
            return null;
        }
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

    public int getCurrentPosition() {
        return MEDIA_PLAYER.getCurrentPosition();
    }

    private void pause() {
        actionIntent.setAction(ACTION_PAUSE);
        localBroadcastManager.sendBroadcast(actionIntent);
        MEDIA_PLAYER.pause();
    }

    private void seekTo(final Control control) {
        MEDIA_PLAYER.seekTo(control.getValue());
    }

    private void start(Control control) {
        actionIntent.setAction(ACTION_PLAY);
        localBroadcastManager.sendBroadcast(actionIntent);
        try {
            if (control.getName() != null) {
                MEDIA_PLAYER.reset();
                try {
                    MEDIA_PLAYER.setDataSource(root + control.getName());
                    MEDIA_PLAYER.prepare();
                    MEDIA_PLAYER.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                title = root + control.getName();
            }
            MEDIA_PLAYER.start();

        } catch (Exception ignored) {
        }
    }

    public boolean isPlaying() {
        return MEDIA_PLAYER.isPlaying();
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}








