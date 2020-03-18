package com.vkpapps.soundbooster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.AudioAdapter;
import com.vkpapps.soundbooster.interfaces.OnHostSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HostSongFragment extends Fragment implements AudioAdapter.OnAudioSelectedListener {

    private File song;
    private OnHostSongFragmentListener onHostSongFragmentListener;
    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private List<AudioModel> allSong;
    private AudioAdapter audioAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        song = view.getContext().getDir("song", Context.MODE_PRIVATE);
        if (PermissionUtils.checkStoragePermission(view.getContext())) {
            allSong = new ArrayList<>();
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            refreshSong();
            audioAdapter = new AudioAdapter(allSong, this);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    if (onNavigationVisibilityListener != null)
                        onNavigationVisibilityListener.onNavVisibilityChange(velocityY > 0);
                    return false;
                }
            });
            recyclerView.setAdapter(audioAdapter);
            audioAdapter.notifyDataSetChanged();

        } else {
            PermissionUtils.askStoragePermission(getActivity());
        }


    }

    @Override
    public void onAudioSelected(AudioModel audioMode) {
        onHostSongFragmentListener.onHostAudioSelected(audioMode);
    }

    @Override
    public void onAudioLongSelected(AudioModel audioModel) {

    }

    public void refreshSong() {
        allSong.clear();
        for (File file : Objects.requireNonNull(song.listFiles())) {
            AudioModel audioModel = new AudioModel();
            audioModel.setName(file.getName());
            audioModel.setPath(file.getPath());
            allSong.add(audioModel);
        }
        if (audioAdapter != null) {
            audioAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSong();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHostSongFragmentListener) {
            onHostSongFragmentListener = (OnHostSongFragmentListener) context;
        }
        if (context instanceof OnNavigationVisibilityListener) {
            onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        onHostSongFragmentListener = null;
        onNavigationVisibilityListener = null;
    }
}
