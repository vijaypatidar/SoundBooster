package com.vkpapps.soundbooster.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.interfaces.OnCommandListener;
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener;
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerFragment extends Fragment implements View.OnClickListener, OnMediaPlayerChangeListener {

    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private OnFragmentAttachStatusListener onFragmentAttachStatusListener;
    private TextView audioTitle;
    private ImageView audioCover, btnPlay;
    private MediaPlayer mediaPlayer;
    private File root;
    private OnCommandListener commandListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = getActivity().getDir("song", Context.MODE_PRIVATE);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        view.findViewById(R.id.btnNext).setOnClickListener(this);
        view.findViewById(R.id.btnPrevious).setOnClickListener(this);
        audioCover = view.findViewById(R.id.audioCover);
        audioTitle = view.findViewById(R.id.audioTitle);
        if (onFragmentAttachStatusListener != null)
            onFragmentAttachStatusListener.onFragmentAttached(this);

        AppCompatSeekBar appCompatSeekBar = view.findViewById(R.id.seekBar);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    long totalDuration = mediaPlayer.getDuration();
                    long currentDuration = mediaPlayer.getCurrentPosition();
                    int per = (int) (currentDuration * 100 / totalDuration);
                    appCompatSeekBar.setProgress(per);
                } catch (Exception ignored) {
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Log.d("seekto", "onProgressChanged: " + progress + " " + fromUser);
                    int time = progress * mediaPlayer.getDuration() / 100;
                    String command = "SKT " + time;
                    commandListener.onCommandCreated(command);
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
    public void onClick(View v) {
        String com;
        switch (v.getId()) {
            case R.id.btnPlay:
                if (mediaPlayer.isPlaying()) {
                    com = "PAS";
                } else {
                    com = "PLY " + audioTitle.getText().toString();
                }
                commandListener.onCommandCreated(com);
                break;

        }
    }

    @Override
    public void onChangeSong(String title, MediaPlayer mediaPlayer) {
        audioTitle.setText(title);
        loadCover(title);
        this.mediaPlayer = mediaPlayer;
        setPlayPauseButton();
    }

    @Override
    public void onPlayingStatusChange(boolean isPlaying) {
        setPlayPauseButton();
    }

    private void loadCover(String title) {
        File file = new File(Utils.imageRoot, title);
        if (file.exists())
            audioCover.setImageURI(Uri.fromFile(file));

    }

    @Override
    public void onVolumeChange(float volume) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        onFragmentAttachStatusListener = (OnFragmentAttachStatusListener) context;
        commandListener = (OnCommandListener) context;
        onNavigationVisibilityListener.onNavVisibilityChange(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentAttachStatusListener.onFragmentDetached(this);
        onNavigationVisibilityListener.onNavVisibilityChange(true);
        onNavigationVisibilityListener = null;
        onFragmentAttachStatusListener = null;
        commandListener = null;
    }

    private void setPlayPauseButton() {
        btnPlay.setImageResource(mediaPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
    }
}
