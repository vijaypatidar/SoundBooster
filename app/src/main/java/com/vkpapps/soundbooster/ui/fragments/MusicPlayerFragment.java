package com.vkpapps.soundbooster.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;
import com.vkpapps.soundbooster.App;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener;
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnObjectCallbackListener;
import com.vkpapps.soundbooster.model.control.ControlPlayer;
import com.vkpapps.soundbooster.utils.StorageManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author VIJAY PATIDAR
 */
public class MusicPlayerFragment extends Fragment implements View.OnClickListener, OnMediaPlayerChangeListener {

    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private OnFragmentAttachStatusListener onFragmentAttachStatusListener;
    private TextView audioTitle;
    private ImageView audioCover, btnPlay;
    private AppCompatImageView btnDownload;
    private MediaPlayer mediaPlayer = App.getMusicPlayerHelper().getMediaPlayer();
    private OnObjectCallbackListener objectCallbackListener;
    private StorageManager storageManager;
    private Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageManager = new StorageManager(view.getContext());
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        view.findViewById(R.id.btnNext).setOnClickListener(this);
        view.findViewById(R.id.btnPrevious).setOnClickListener(this);
        audioCover = view.findViewById(R.id.audioCover);
        audioTitle = view.findViewById(R.id.audioTitle);
        btnDownload = view.findViewById(R.id.btnDownload);
        onFragmentAttachStatusListener.onFragmentAttached(this);

        AppCompatSeekBar appCompatSeekBar = view.findViewById(R.id.seekBar);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    long totalDuration = mediaPlayer.getDuration();
                    long currentDuration = mediaPlayer.getCurrentPosition();
                    int per = (int) (currentDuration * 100 / totalDuration);
                    appCompatSeekBar.setProgress(per);
                    if (per == 100) {
                        objectCallbackListener.onObjectCreated(new ControlPlayer(ControlPlayer.ACTION_NEXT, ""));
                    }
                } catch (Exception ignored) {
                }
            }
        }, 0, 1000);

        ControlPlayer controlPlayer = new ControlPlayer();
        controlPlayer.setAction(ControlPlayer.ACTION_SEEK_TO);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int time = progress * mediaPlayer.getDuration() / 100;
                    controlPlayer.setIntData(time);
                    objectCallbackListener.onObjectCreated(controlPlayer);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String currentSongName = App.getMusicPlayerHelper().getCurrentSongName();
        if (currentSongName != null)
            onChangeSong(currentSongName);
        else
            Navigation.findNavController(view).popBackStack();
    }


    @Override
    public void onClick(View v) {
        ControlPlayer controlPlayer = new ControlPlayer();
        switch (v.getId()) {
            case R.id.btnPlay:
                controlPlayer.setAction(mediaPlayer.isPlaying() ? ControlPlayer.ACTION_PAUSE : ControlPlayer.ACTION_RESUME);
                break;
            case R.id.btnNext:
                controlPlayer.setAction(ControlPlayer.ACTION_NEXT);
                break;
            case R.id.btnPrevious:
                controlPlayer.setAction(ControlPlayer.ACTION_PREVIOUS);
        }
        objectCallbackListener.onObjectCreated(controlPlayer);
    }

    @Override
    public void onChangeSong(@NotNull String title) {
        audioTitle.setText(title);
        loadCover(title);
        setPlayPauseButton(mediaPlayer.isPlaying());
        checkDownload(title);
    }

    @Override
    public void onPlayingStatusChange(boolean isPlaying) {
        setPlayPauseButton(isPlaying);
    }

    private void loadCover(String title) {
        File file = new File(storageManager.getImageDir(), title);
        if (file.exists()) {
            Picasso.get().load(Uri.fromFile(file)).into(audioCover);
        }
    }

    @Override
    public void onVolumeChange(float volume) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        onFragmentAttachStatusListener = (OnFragmentAttachStatusListener) context;
        objectCallbackListener = (OnObjectCallbackListener) context;
        onNavigationVisibilityListener.onNavVisibilityChange(false);
    }

    @Override
    public void onDetach() {
        onFragmentAttachStatusListener.onFragmentDetached(this);
        onNavigationVisibilityListener.onNavVisibilityChange(true);
        onNavigationVisibilityListener = null;
        onFragmentAttachStatusListener = null;
        objectCallbackListener = null;
        if (timer != null) {
            timer.cancel();
        }
        super.onDetach();
    }

    private void setPlayPauseButton(boolean isPlaying) {
        btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void checkDownload(@NonNull String name) {
        if (storageManager.isSongDownloaded(name)) {
            downloaded();
        } else {
            btnDownload.setOnClickListener(v -> {
                storageManager.download(name, source -> downloaded());
            });
        }
    }

    private void downloaded() {
        btnDownload.setImageResource(R.drawable.ic_check_circle);
        btnDownload.setOnClickListener(v -> Toast.makeText(requireContext(), "Already downloaded", Toast.LENGTH_SHORT).show());
    }

}
