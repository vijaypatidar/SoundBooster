package com.vkpapps.soundbooster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.model.User;

import java.util.ArrayList;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyHolder> {

    public static ArrayList<User> users = new ArrayList<>();


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
        holder.switchAllow.setChecked(user.isSharingAllowed());
        holder.switchAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                user.setSharingAllowed(b);
                Server.getInstance().sendRule(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private Switch switchAllow;
        private TextView userName;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            switchAllow = itemView.findViewById(R.id.switchAllow);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
