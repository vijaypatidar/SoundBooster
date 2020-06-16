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
import com.vkpapps.soundbooster.adapter.ClientAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener;
import com.vkpapps.soundbooster.interfaces.OnUsersUpdateListener;

import java.util.List;

/**
 * @author VIJAY PATIDAR
 * */
public class DashboardFragment extends Fragment implements OnUsersUpdateListener {

    private List<ClientHelper> users;
    private OnUserListRequestListener onUserListRequestListener;
    private ClientAdapter clientAdapter;
    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private OnFragmentAttachStatusListener onFragmentAttachStatusListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Nothing to display when user is client
        if (users == null) return;

        clientAdapter = new ClientAdapter(users, view.getContext());
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
        onUserListRequestListener = (OnUserListRequestListener) context;
        onFragmentAttachStatusListener = (OnFragmentAttachStatusListener) context;
        onFragmentAttachStatusListener.onFragmentAttached(this);
        users = onUserListRequestListener.onRequestUsers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentAttachStatusListener.onFragmentDetached(this);
        onUserListRequestListener = null;
        onNavigationVisibilityListener = null;
        onFragmentAttachStatusListener = null;
    }


    @Override
    public void onUserUpdated() {
        if (clientAdapter != null)
            clientAdapter.notifyDataSetChanged();
    }
}
