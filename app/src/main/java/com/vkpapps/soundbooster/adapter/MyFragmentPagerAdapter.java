package com.vkpapps.soundbooster.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Fragment> fragments) {
        super(fm, behavior);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Host Music";
            case 1:
                return "Music Player";
            case 2:
                return "Local Music";
            default:
                return "Pending Requests";
        }
    }
}
