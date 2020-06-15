package com.vkpapps.soundbooster.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.FirebaseUtils;
import com.vkpapps.soundbooster.utils.StorageManager;

import java.io.File;
import java.util.List;

/**
 * @author VIJAY PATIDAR
 * */
public class HostedAudioAdapter extends RecyclerView.Adapter<HostedAudioAdapter.AudioViewHolder> {
    private List<AudioModel> audioModels;
    private OnAudioSelectedListener onAudioSelectedListener;
    private StorageManager storageManager;

    public HostedAudioAdapter(List<AudioModel> audioModels, OnAudioSelectedListener onAudioSelectedListener, Context context) {
        this.audioModels = audioModels;
        this.onAudioSelectedListener = onAudioSelectedListener;
        this.storageManager = new StorageManager(context);
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate;
        if (viewType == 1) {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_list_item, parent, false);
        } else {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_list_item_ad_view, parent, false);
        }
        return new AudioViewHolder(inflate);
    }

    @Override
    public int getItemViewType(int position) {
        return audioModels.get(position) == null ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioModel audioModel = audioModels.get(position);
        if (audioModel == null) {
            AdView adView = (AdView) holder.itemView;
            FirebaseUtils.getAdRequest(adView);
        } else {

            holder.audioTitle.setText(audioModel.getName());
            holder.audioArtist.setText(audioModel.getArtist());
            holder.itemView.setOnClickListener(v -> onAudioSelectedListener.onAudioSelected(audioModel));
            holder.itemView.setOnLongClickListener(v -> {
                onAudioSelectedListener.onAudioLongSelected(audioModel);
                return true;
            });

            ImageView audioIcon = holder.audioIcon;
            File file = new File(storageManager.getImageDir(), audioModel.getName());
            if (file.exists()) {
                Picasso.get().load(Uri.fromFile(file)).into(audioIcon);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (audioModels == null) ? 0 : audioModels.size();
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