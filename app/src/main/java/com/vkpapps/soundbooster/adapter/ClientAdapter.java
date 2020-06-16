package com.vkpapps.soundbooster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.model.User;

import java.util.List;

/**
 * @author VIJAY PATIDAR
 */
public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyHolder> {

    private List<ClientHelper> users;

    public ClientAdapter(List<ClientHelper> users, Context context) {
        this.users = users;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final ClientHelper clientHelper = users.get(position);
        final User user = clientHelper.getUser();
        holder.userName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return (users == null) ? 0 : users.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        private TextView userName;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
        }
    }
}
