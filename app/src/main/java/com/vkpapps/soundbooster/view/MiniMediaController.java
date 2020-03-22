package com.vkpapps.soundbooster.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vkpapps.soundbooster.R;

import java.io.File;

public class MiniMediaController extends FrameLayout {
    private ImageView audioCover;
    private TextView audioTitle;

    public MiniMediaController(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    public MiniMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MiniMediaController(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View inflate = inflate(getContext(), R.layout.mini_controller, this);
        audioCover = inflate.findViewById(R.id.audioCover);
        audioTitle = inflate.findViewById(R.id.audioTitle);
    }

    public void changeSong(String name, File root) {
        setVisibility(VISIBLE);
        audioTitle.setText(name);
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(new File(root, name).getAbsolutePath());

            byte[] data = mmr.getEmbeddedPicture();

            // convert the byte array to a bitmap
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                audioCover.setImageBitmap(bitmap); //associated cover art in bitmap
            }

            audioCover.setAdjustViewBounds(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}