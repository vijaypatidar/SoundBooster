package com.vkpapps.soundbooster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.FileRequest;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    private final ArrayList<FileRequest> requests;

    public RequestAdapter(ArrayList<FileRequest> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FileRequest request = requests.get(position);
        holder.byName.setText("host");
        holder.songTitle.setText(request.getName());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView songTitle;
        private final TextView byName;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            byName = itemView.findViewById(R.id.byName);
        }

    }
}
