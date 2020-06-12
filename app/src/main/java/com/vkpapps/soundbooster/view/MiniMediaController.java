package com.vkpapps.soundbooster.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.utils.StorageManager;

import java.io.File;

/**
 * @author VIJAY PATIDAR
 */

public class MiniMediaController extends FrameLayout {
    private ImageView audioCover;
    private TextView audioTitle;
    private ImageButton btnPlay;
    private boolean enableVisibilityChanges = true;
    private File imageRoot;

    public void setEnableVisibilityChanges(boolean enableVisibilityChanges) {
        this.enableVisibilityChanges = enableVisibilityChanges;
    }

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
        // this is passed as root param ,so that view is attached to FrameLayout i.e MiniMediaController
        View inflate = inflate(getContext(), R.layout.mini_controller, this);
        audioCover = inflate.findViewById(R.id.audioCover);
        audioTitle = inflate.findViewById(R.id.audioTitle);
        btnPlay = inflate.findViewById(R.id.btnPlay);
        imageRoot = new StorageManager(getContext()).getImageDir();
    }

    public void changeSong(String name) {
        if (enableVisibilityChanges) {
            setVisibility(VISIBLE);
        }
        audioTitle.setText(name);
        File file = new File(imageRoot, name);
        if (file.exists()) {
            Picasso.get().load(Uri.fromFile(file)).into(audioCover);
        }
    }

    public void setButtonOnClick(View.OnClickListener click) {
        btnPlay.setOnClickListener(click);
    }

    public void changePlayButtonIcon(boolean isPlaying) {
        btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
}