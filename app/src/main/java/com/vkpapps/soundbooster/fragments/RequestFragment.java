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
import com.vkpapps.soundbooster.adapter.RequestAdapter;
import com.vkpapps.soundbooster.model.FileRequest;

import java.util.ArrayList;

public class RequestFragment extends Fragment {

    private RequestAdapter requestAdapter;
    private final ArrayList<FileRequest> requestArrayList;

    public RequestFragment(ArrayList<FileRequest> requestArrayList) {
        this.requestArrayList = requestArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_song, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        requestAdapter = new RequestAdapter(requestArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(requestAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            requestAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }
}
