package com.vkpapps.soundbooster;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.vkpapps.soundbooster.model.Control;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView songTitle;
    private ImageView songPic;
    private SeekBar seekBar;

    private MusicPlayerService musicSrv;
    private final ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            Bitmap bitmap = musicSrv.getBitmapOfCurrentSong();
            if (bitmap != null)
                songPic.setImageBitmap(bitmap);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
    }

    private void initUI() {
        songPic = findViewById(R.id.songPic);
        songTitle = findViewById(R.id.songTitle);
        ImageView btnPrev = findViewById(R.id.btnPrev);
        ImageView btnNext = findViewById(R.id.btnNext);
        ImageView btnPlay = findViewById(R.id.btnPlay);
        Button btnSync = findViewById(R.id.btnSync);

        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnSync.setOnClickListener(this);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
