package com.vkpapps.soundbooster.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.activity.PartyActivity;
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

    private ArrayList<LocalSong> localSongArrayList = new ArrayList<>();
    private ArrayList<LocalSong> allSong = new ArrayList<>();

    private final OnLocalSongFragmentListener onLocalSongFragmentListener;
    private final String root;

    public LocalSongFragment(OnLocalSongFragmentListener onLocalSongFragmentListener, String root) {
        this.onLocalSongFragmentListener = onLocalSongFragmentListener;
        this.root = root;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        final LocalMusicAdapter localMusicAdapter = new LocalMusicAdapter(localSongArrayList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(localMusicAdapter);

        final List<File> allAudios = getAllAudios(view.getContext());
        for (int i = 0; i < allAudios.size(); i++) {
            File file = allAudios.get(i);
            allSong.add(new LocalSong(file.getPath(), file.getName()));
        }

        localSongArrayList.addAll(allSong);
        localMusicAdapter.notifyDataSetChanged();

        SearchView searchView = view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                localSongArrayList.clear();
                for (LocalSong localSong : allSong) {
                    if (localSong.getName().toLowerCase().contains(newText.toLowerCase())) {
                        localSongArrayList.add(localSong);
                    }
                }
                localMusicAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public void onLocalMusicSelect(final int position) {
        if (PartyActivity.user.isSharingAllowed()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
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
                }
            }).start();
            onLocalSongFragmentListener.onSelectLocalMusic(localSongArrayList.get(position));
        } else {
            Toast.makeText(getContext(), "host decline", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnLocalSongFragmentListener {
        void onSelectLocalMusic(LocalSong localSong);
    }
}
