package com.vkpapps.soundbooster;

import android.media.MediaPlayer;

import java.io.IOException;

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


    public void seek(final int seekTo, long date) {
        mediaPlayer.seekTo(seekTo);
    }

    public void play(long date) {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        mediaPlayer.start();
//            }
//        }, date - System.currentTimeMillis());

    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void next() {

    }

    public int calculateSeek(int percentage){
        int total = mediaPlayer.getDuration();
        return total*percentage/100;
    }
    public void prev() {
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
