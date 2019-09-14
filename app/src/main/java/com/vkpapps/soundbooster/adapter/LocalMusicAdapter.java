package com.vkpapps.soundbooster.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MyViewHolder> {

    private ArrayList<LocalSong> songArrayList;
    private OnItemClickListener onItemClickListener;

    public LocalMusicAdapter(ArrayList<LocalSong> songArrayList, OnItemClickListener onItemClickListener) {
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
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(localSong.getPath());

            byte[] data = mmr.getEmbeddedPicture();

            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                holder.imageView.setImageBitmap(bitmap); //associated cover art in bitmap
            }
        } catch (Exception ignored) {

        }
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onLocalMusicSelect(position);
                }


            });
        }
    }
}
