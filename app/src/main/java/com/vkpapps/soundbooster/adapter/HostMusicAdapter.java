package com.vkpapps.soundbooster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.LocalSong;

import java.util.ArrayList;

public class HostMusicAdapter extends RecyclerView.Adapter<HostMusicAdapter.MyViewHolder> {

    private ArrayList<LocalSong> songArrayList;
    private OnItemClickListener onItemClickListener;

    public HostMusicAdapter(ArrayList<LocalSong> songArrayList, OnItemClickListener onItemClickListener) {
        this.songArrayList = songArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LocalSong localSong = songArrayList.get(position);
        holder.setClickListener(position);
        holder.songTitle.setText(localSong.getName());
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public interface OnItemClickListener {
        void onLocalMusicSelect(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView songTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.songPic);
            songTitle = itemView.findViewById(R.id.songTitle);
        }

        void setClickListener(final int position) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLocalMusicSelect(position);
                    return false;
                }
            });
        }
    }
}
