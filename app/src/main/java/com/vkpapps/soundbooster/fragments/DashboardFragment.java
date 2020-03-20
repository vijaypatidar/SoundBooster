package com.vkpapps.soundbooster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.ClientAdapter;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.model.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private List<User> users;
    private OnUserListRequestListener onUserListRequestListener;
    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ClientAdapter clientAdapter = new ClientAdapter(users,view.getContext());
        RecyclerView recyclerView = view.findViewById(R.id.clientList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (onNavigationVisibilityListener != null)
                    onNavigationVisibilityListener.onNavVisibilityChange(velocityY < 0);
                return false;
            }
        });
        recyclerView.setAdapter(clientAdapter);
        clientAdapter.notifyDataSetChanged();

        Log.d("TAG", "onViewCreated: ============================== ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView:============================ ");
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        onUserListRequestListener = (OnUserListRequestListener) context;
        users = onUserListRequestListener.onRequestUsers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUserListRequestListener = null;
        onNavigationVisibilityListener = null;
    }


}
