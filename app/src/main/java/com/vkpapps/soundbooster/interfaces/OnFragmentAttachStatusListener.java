package com.vkpapps.soundbooster.interfaces;

import androidx.fragment.app.Fragment;

public interface OnFragmentAttachStatusListener {
    void onFragmentAttached(Fragment fragment);
    void onFragmentDetached(Fragment fragment);
}
