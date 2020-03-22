package com.vkpapps.soundbooster.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.util.List;

public class HostedAudioAdapter extends RecyclerView.Adapter<HostedAudioAdapter.AudioViewHolder> {
    private List<AudioModel> audioModels;
    private OnAudioSelectedListener onAudioSelectedListener;

    public HostedAudioAdapter(List<AudioModel> audioModels, OnAudioSelectedListener onAudioSelectedListener) {
        this.audioModels = audioModels;
        this.onAudioSelectedListener = onAudioSelectedListener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_list_item, parent, false);
        return new AudioViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioModel audioModel = audioModels.get(position);
        holder.audioTitle.setText(audioModel.getName());
        holder.audioArtist.setText(audioModel.getArtist());
        holder.itemView.setOnClickListener(v -> {
            onAudioSelectedListener.onAudioSelected(audioModel);
        });
        holder.itemView.setOnLongClickListener(v -> {
            onAudioSelectedListener.onAudioLongSelected(audioModel);
            return true;
        });

        ImageView audioIcon = holder.audioIcon;
        File file = new File(Utils.imageRoot, audioModel.getName());
        if (file.exists())
            audioIcon.setImageURI(Uri.fromFile(file));
    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    public interface OnAudioSelectedListener {
        void onAudioSelected(AudioModel audioMode);

        void onAudioLongSelected(AudioModel audioModel);
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView audioTitle, audioArtist;
        ImageView audioIcon;

        AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            audioIcon = itemView.findViewById(R.id.audio_icon);
            audioTitle = itemView.findViewById(R.id.audio_title);
            audioArtist = itemView.findViewById(R.id.audio_artist);
        }
    }
}