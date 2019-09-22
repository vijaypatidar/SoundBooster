package com.vkpapps.soundbooster.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.services.MusicPlayerService;
import com.vkpapps.soundbooster.utils.Utils;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private SeekBar seekBar;
    private ImageView btnPlay;
    private ImageView songPic;
    private TextView songTitle;
    private MusicPlayerService musicSrv;
    private boolean isHost;

    private final ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicSrv = binder.getService();
            Bitmap bitmap = musicSrv.getBitmapOfCurrentSong();
            if (bitmap != null) {
                songPic.setImageBitmap(bitmap);
            }
            songTitle.setText(musicSrv.getCurrentTitle());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MusicPlayerService.ACTION_PLAY:
                        btnPlay.setImageBitmap(Utils.getBitmap(MusicPlayerActivity.this, R.drawable.ic_action_pause));
                        break;
                    case MusicPlayerService.ACTION_PAUSE:
                        btnPlay.setImageBitmap(Utils.getBitmap(MusicPlayerActivity.this, R.drawable.ic_action_play));
                        break;
                    case MusicPlayerService.ACTION_UPDATE_PROGRESS:
                        seekBar.setProgress(intent.getIntExtra("progress", 0));
                        break;
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        Control control = null;
        switch (view.getId()) {
            case R.id.btnPlay:
                control = new Control(musicSrv.isPlaying() ? Control.PAUSE : Control.PLAY, System.currentTimeMillis() + 3500, null);
                break;
            case R.id.btnSync:
                control = new Control(Control.SEEK, System.currentTimeMillis() + 3500, musicSrv.getCurrentPosition());
                break;
            case R.id.btnPrev:
                break;
            case R.id.btnNext:
                break;
        }
        if (control != null) {
            sendControl(control);
            musicSrv.processControlRequest(control);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (musicSrv == null)
            bindService(new Intent(this, MusicPlayerService.class), musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);
        initUI();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.ACTION_PLAY);
        intentFilter.addAction(MusicPlayerService.ACTION_PAUSE);
        intentFilter.addAction(MusicPlayerService.ACTION_UPDATE_PROGRESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
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
                if (b) {
                    Control control = new Control(Control.SEEK, System.currentTimeMillis() + 3500, musicSrv.getCalculatedSeek(i));
                    sendControl(control);
                    musicSrv.processControlRequest(control);
                }

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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
        unbindService(musicConnection);
    }

    private void sendControl(Control control) {
        if (isHost) {
            Server.getInstance().send(control, null);
        } else {
            PartyActivity.clientHelper.send(control);
        }
    }
}
