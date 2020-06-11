package com.vkpapps.soundbooster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.AudioAdapter;
import com.vkpapps.soundbooster.interfaces.OnLocalSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.PermissionUtils;
import com.vkpapps.soundbooster.utils.StorageManager;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LocalSongFragment extends Fragment implements AudioAdapter.OnAudioSelectedListener {

    private OnLocalSongFragmentListener onLocalSongFragmentListener;
    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private List<AudioModel> allSong;
    private StorageManager storageManager;

    public LocalSongFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageManager = new StorageManager(getActivity());

        if (PermissionUtils.checkStoragePermission(view.getContext())) {
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            allSong = Utils.getAllAudioFromDevice(view.getContext());
            sort();
            AudioAdapter audioAdapter = new AudioAdapter(allSong, this);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(audioAdapter);
            recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    onNavigationVisibilityListener.onNavVisibilityChange(velocityY < 0);
                    return false;
                }
            });
            audioAdapter.notifyDataSetChanged();
        } else {
            Navigation.findNavController(view).popBackStack();
            PermissionUtils.askStoragePermission(getActivity(), 101);
        }
    }

    @Override
    public void onAudioSelected(AudioModel audioMode) {
        storageManager.copySong(new File(audioMode.getPath()), source ->
                onLocalSongFragmentListener.onLocalSongSelected(audioMode));
    }

    @Override
    public void onAudioLongSelected(AudioModel audioModel) {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLocalSongFragmentListener) {
            onLocalSongFragmentListener = (OnLocalSongFragmentListener) context;
        }
        if (context instanceof OnNavigationVisibilityListener) {
            onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onLocalSongFragmentListener = null;
        onNavigationVisibilityListener = null;
    }

    private void sort() {
        Collections.sort(allSong, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        for (int i = 5; i < allSong.size(); i += 20) {
            allSong.add(i, null);
        }
    }

}
