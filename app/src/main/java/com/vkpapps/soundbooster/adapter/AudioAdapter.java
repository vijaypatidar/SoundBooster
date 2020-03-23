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

import com.google.android.gms.ads.AdView;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.utils.FirebaseUtils;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    private List<AudioModel> audioModels;
    private OnAudioSelectedListener onAudioSelectedListener;

    public AudioAdapter(List<AudioModel> audioModels, OnAudioSelectedListener onAudioSelectedListener) {
        this.audioModels = audioModels;
        this.onAudioSelectedListener = onAudioSelectedListener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate;
        if (viewType == 1) {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_list_item, parent, false);
        } else {
            inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_list_item_ad_view, parent, false);
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
            adView.loadAd(FirebaseUtils.getAdRequest());
        } else {

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

            try {
                android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(audioModel.getPath());
                byte[] data = mmr.getEmbeddedPicture();
                // convert the byte array to a bitmap
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    audioIcon.setImageBitmap(bitmap); //associated cover art in bitmap
                }
                audioIcon.setAdjustViewBounds(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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