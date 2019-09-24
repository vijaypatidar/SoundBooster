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
import com.vkpapps.soundbooster.adapter.ClientAdapter;

public class ClientControlFragment extends Fragment {

    private ClientAdapter clientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_control, container, false);
        clientAdapter = new ClientAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.clientList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(clientAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clientAdapter.notifyDataSetChanged();
    }
}
