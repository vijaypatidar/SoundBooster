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
import com.vkpapps.soundbooster.model.HostSong;

import java.util.ArrayList;

public class HostMusicAdapter extends RecyclerView.Adapter<HostMusicAdapter.MyViewHolder> {

    private ArrayList<HostSong> songArrayList;
    private OnItemClickListener onItemClickListener;

    public HostMusicAdapter(ArrayList<HostSong> songArrayList, OnItemClickListener onItemClickListener) {
        this.songArrayList = songArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hosted_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HostSong hostSong = songArrayList.get(position);
        holder.setClickListener(position);
        holder.songTitle.setText(hostSong.getPath());
        if (hostSong.isAvailable()) {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(hostSong.getPath());

            byte[] data = mmr.getEmbeddedPicture();

            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                holder.imageView.setImageBitmap(bitmap); //associated cover art in bitmap
            }
        }
//        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        holder.imageView.setAdjustViewBounds(true);
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
        private TextView playedBy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.songPic);
            songTitle = itemView.findViewById(R.id.songTitle);
            playedBy = itemView.findViewById(R.id.playedBy);
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
