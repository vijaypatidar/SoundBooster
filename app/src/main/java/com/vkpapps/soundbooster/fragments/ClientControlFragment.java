package com.vkpapps.soundbooster.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.vkpapps.soundbooster.adapter.ClientAdapter;
import com.vkpapps.soundbooster.connection.CommandHelperRunnable;
import com.vkpapps.soundbooster.connection.ServerHelper;
import com.vkpapps.soundbooster.model.User;

import java.util.ArrayList;

public class ClientControlFragment extends Fragment {

    private ServerHelper serverHelper;

    public ClientControlFragment(ServerHelper serverHelper) {
        this.serverHelper = serverHelper;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<User> users = new ArrayList<>();
        for (CommandHelperRunnable commandHelperRunnable : serverHelper.getCommandHelperRunnables()) {
            users.add(commandHelperRunnable.user);
        }
        ClientAdapter clientAdapter = new ClientAdapter(users);
        RecyclerView recyclerView = view.findViewById(R.id.clientList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(clientAdapter);
        clientAdapter.notifyDataSetChanged();

        Log.d("TAG", "onViewCreated: ============================== ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView:============================ ");
        return inflater.inflate(R.layout.fragment_client_control, container, false);
    }

}
