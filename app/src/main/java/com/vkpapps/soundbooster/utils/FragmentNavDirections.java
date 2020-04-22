package com.vkpapps.soundbooster.utils;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;

import com.vkpapps.soundbooster.R;

public class FragmentNavDirections {
    public static NavDirections moveToMusicPlayer() {
        return new NavDirections() {
            @Override
            public int getActionId() {
                return R.id.action_navigation_home_to_navigation_musicPlayer;
            }

            @NonNull
            @Override
            public Bundle getArguments() {
                return null;
            }
        };
    }
}
