package com.vkpapps.soundbooster.fragments;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.vkpapps.soundbooster.MediaPlayerService;
import com.vkpapps.soundbooster.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment {


    private AppCompatImageView btnPlay, btnNext, btnPrev;
    private TextView songTitle;
    private ImageView songPic;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private MediaPlayerService mediaPlayerService;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnPlay = view.findViewById(R.id.btnPlay);
        songTitle = view.findViewById(R.id.songTitle);
        songPic = view.findViewById(R.id.songPic);
        seekBar = view.findViewById(R.id.seekBar);

        mediaPlayerService = MediaPlayerService.getInstance();
        mediaPlayer = mediaPlayerService.getMediaPlayer();
        return view;
    }

    private void init() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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
