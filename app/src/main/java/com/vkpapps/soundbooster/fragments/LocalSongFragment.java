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
import com.vkpapps.soundbooster.adapter.LocalMusicAdapter;
import com.vkpapps.soundbooster.model.LocalSong;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vkpapps.soundbooster.utils.Utils.getAllAudios;

public class LocalSongFragment extends Fragment implements LocalMusicAdapter.OnItemClickListener {

    private ArrayList<LocalSong> localSongArrayList;
    private OnLocalSongFragmentListener onLocalSongFragmentListener;
    private String root;

    public LocalSongFragment(OnLocalSongFragmentListener onLocalSongFragmentListener, String root) {
        this.onLocalSongFragmentListener = onLocalSongFragmentListener;
        this.root = root;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_song, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        localSongArrayList = new ArrayList<>();
        LocalMusicAdapter localMusicAdapter = new LocalMusicAdapter(localSongArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(localMusicAdapter);
        final List<File> allAudios = getAllAudios(view.getContext());
        for (int i = 0; i < allAudios.size(); i++) {
            File file = allAudios.get(i);
            localSongArrayList.add(new LocalSong(file.getPath(), file.getName()));
        }
        localMusicAdapter.notifyDataSetChanged();
        return view;
    }


    @Override
    public void onLocalMusicSelect(int position) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(localSongArrayList.get(position).getPath()));
            FileOutputStream fileOutputStream = new FileOutputStream(new File(root + File.separator + localSongArrayList.get(position).getName()));
            byte[] bytes = new byte[1024 * 2];
            int read;
            while ((read = fileInputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, read);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onLocalSongFragmentListener.onSelectLocalMusic(localSongArrayList.get(position));
    }

    public interface OnLocalSongFragmentListener {
        void onSelectLocalMusic(LocalSong localSong);
    }
}
