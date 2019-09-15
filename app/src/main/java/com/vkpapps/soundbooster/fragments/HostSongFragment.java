package com.vkpapps.soundbooster.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.HostMusicAdapter;
import com.vkpapps.soundbooster.model.HostSong;

import java.io.File;
import java.util.ArrayList;

public class HostSongFragment extends Fragment implements HostMusicAdapter.OnItemClickListener {


    private ArrayList<HostSong> hostSongs;
    private OnHostSongFragmentListener onHostSongFragmentListener;
    private String root;
    private HostMusicAdapter hostMusicAdapter;

    public HostSongFragment(OnHostSongFragmentListener onHostSongFragmentListener, String root) {
        this.onHostSongFragmentListener = onHostSongFragmentListener;
        this.root = root;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_song, container, false);
        RecyclerView recyclerView = (RecyclerView) view;

        hostSongs = new ArrayList<>();
        hostMusicAdapter = new HostMusicAdapter(hostSongs, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hostMusicAdapter);
        return view;
    }

    @Override
    public void onLocalMusicSelect(int position) {
        onHostSongFragmentListener.onMusicSelectToPlay(hostSongs.get(position));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        File[] allAudios = new File(root).listFiles();
        assert allAudios != null;
        hostSongs.clear();
        for (File file : allAudios) {
            hostSongs.add(new HostSong(file.getPath(), file.getName(), true));
        }
        hostMusicAdapter.notifyDataSetChanged();
    }

    public interface OnHostSongFragmentListener {
        void onMusicSelectToPlay(HostSong hostSong);
    }
}
