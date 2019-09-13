package com.vkpapps.soundbooster;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerService {
    private static final MediaPlayerService ourInstance = new MediaPlayerService();
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private MediaPlayerService() {
    }

    public static MediaPlayerService getInstance() {
        return ourInstance;
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void release() {
        mediaPlayer.release();
    }

    public void load(String index) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(index);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seek(final int seekTo, Date date) {
//        final int to = seekTo * 1000;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                mediaPlayer.seekTo(to);
//            }
//        }, date);
        mediaPlayer.seekTo(0);
    }

    public void play(Date date) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        }, date);

    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void next() {

    }

    public void prev() {
    }
}
