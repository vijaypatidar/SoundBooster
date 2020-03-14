package com.vkpapps.soundbooster.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.AudioAdapter;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.PermissionUtils;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalSongFragment extends Fragment implements AudioAdapter.OnAudioSelectedListener {

    private File song;
    private MusicPlayerHelper musicPlayerHelper;
    private List<AudioModel> selectedSong, allSong;

    public LocalSongFragment(MusicPlayerHelper musicPlayerHelper) {
        this.musicPlayerHelper = musicPlayerHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        song = view.getContext().getDir("song", Context.MODE_PRIVATE);
        if (PermissionUtils.checkStoragePermission(view.getContext())) {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            allSong = Utils.getAllAudioFromDevice(view.getContext());
            selectedSong = new ArrayList<>(allSong);
            AudioAdapter audioAdapter = new AudioAdapter(selectedSong, this);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(audioAdapter);
            audioAdapter.notifyDataSetChanged();

            // searchView
            SearchView searchView = view.findViewById(R.id.search_bar);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    selectedSong.clear();
                    for (AudioModel audioModel : allSong) {
                        if (audioModel.getName().toLowerCase().contains(newText.toLowerCase())) {
                            selectedSong.add(audioModel);
                        }
                    }
                    audioAdapter.notifyDataSetChanged();
                    return false;
                }
            });
        } else {
            PermissionUtils.askStoragePermission(getActivity());
        }


    }

    @Override
    public void onAudioSelected(AudioModel audioMode) {
        new Thread(() -> {
            try {
                Utils.copyFromTo(new File(audioMode.getPath()), new File(song, audioMode.getName()));
                //todo send to other
                //play
                musicPlayerHelper.loadAndPlay(audioMode.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onAudioLongSelected(AudioModel audioModel) {

    }
}
