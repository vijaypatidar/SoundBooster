package com.vkpapps.soundbooster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnClientControlChangeListener;
import com.vkpapps.soundbooster.model.User;

import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyHolder> {

    private List<User> users;
    private OnClientControlChangeListener onClientConnectionStateListener;

    public ClientAdapter(List<User> users, Context context) {
        this.users = users;
        if (context instanceof OnClientControlChangeListener)
            onClientConnectionStateListener = (OnClientControlChangeListener) context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final User user = users.get(position);
        holder.userName.setText(user.getName());
        holder.switchAllow.setChecked(user.isAccess());
        holder.switchAllow.setOnCheckedChangeListener((compoundButton, b) -> {
            if (onClientConnectionStateListener!=null)
                onClientConnectionStateListener.OnClientControlChangeRequest(user);
            //todo add permission manager
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        private Switch switchAllow;
        private TextView userName;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            switchAllow = itemView.findViewById(R.id.switchAllow);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
