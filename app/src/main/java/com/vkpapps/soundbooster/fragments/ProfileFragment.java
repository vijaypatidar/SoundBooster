package com.vkpapps.soundbooster.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vkpapps.soundbooster.App;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.interfaces.OnFragmentPopBackListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.Utils;

/**
 * @author VIJAY PATIDAR
 */
public class ProfileFragment extends Fragment {

    private User user;
    private ImageView imageView;
    private OnNavigationVisibilityListener onNavigationVisibilityListener;
    private OnFragmentPopBackListener onFragmentPopBackListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // This callback will only be called when MyFragment is at least Started.
        return inflater.inflate(R.layout.fragment_user_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = App.getUser();
        imageView = view.findViewById(R.id.userPic);

        EditText editTextName = view.findViewById(R.id.userName);
        editTextName.setText(user.getName());
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            if (!name.isEmpty()) {
                user.setName(name);
                Utils.setUser(user, v.getContext());
                Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                onFragmentPopBackListener.onPopBackStack();
            } else {
                editTextName.setError("name required!");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationVisibilityListener) {
            onNavigationVisibilityListener = (OnNavigationVisibilityListener) context;
            onNavigationVisibilityListener.onNavVisibilityChange(false);
        }
        if (context instanceof OnFragmentPopBackListener) {
            onFragmentPopBackListener = (OnFragmentPopBackListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNavigationVisibilityListener.onNavVisibilityChange(true);
        onNavigationVisibilityListener = null;
        onFragmentPopBackListener = null;
    }
}
