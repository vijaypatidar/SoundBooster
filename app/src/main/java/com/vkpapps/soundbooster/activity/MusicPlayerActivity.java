package com.vkpapps.soundbooster.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.MusicPlayerService;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.Control;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private SeekBar seekBar;
    private ImageView btnPlay;
    private ImageView songPic;
    private TextView songTitle;
    private MusicPlayerService musicSrv;
    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        initUI();
    }

    private final ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            Bitmap bitmap = musicSrv.getBitmapOfCurrentSong();
            if (bitmap != null)
                songPic.setImageBitmap(bitmap);
            songTitle.setText(musicSrv.getCurrentTitle());

            final MediaPlayer mediaPlayer = musicSrv.getMediaPlayer();
            final Handler handler = new Handler();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer.isPlaying()) {
                        try {
                            long totalDuration = mediaPlayer.getDuration();
                            long currentDuration = mediaPlayer.getCurrentPosition();
                            int per = (int) (currentDuration * 100 / totalDuration);
                            seekBar.setProgress(per);
                        } catch (ArithmeticException ignored) {
                        }
                        btnPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_pause));
                    } else {
                        btnPlay.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_play));
                    }
                    handler.postDelayed(this, 1000);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                musicSrv.processControlRequest(new Control(Control.PLAY, 0, null));
                break;
            case R.id.btnSync:
                Control control = new Control(Control.SEEK, 0, musicSrv.getCurrentPosition());
                musicSrv.processControlRequest(control);
                break;
            case R.id.btnPrev:
                musicSrv.moveBy(-1);
                break;
            case R.id.btnNext:
                musicSrv.moveBy(1);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void initUI() {
        songPic = findViewById(R.id.songPic);
        songTitle = findViewById(R.id.songTitle);
        ImageView btnPrev = findViewById(R.id.btnPrev);
        ImageView btnNext = findViewById(R.id.btnNext);
        btnPlay = findViewById(R.id.btnPlay);
        ImageButton btnSync = findViewById(R.id.btnSync);

        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnSync.setOnClickListener(this);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    musicSrv.processControlRequest(new Control(Control.SEEK, 0, musicSrv.getCalculatedSeek(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
