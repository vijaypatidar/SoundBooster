package com.vkpapps.soundbooster.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.vkpapps.soundbooster.App;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.interfaces.OnFragmentPopBackListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.StorageManager;
import com.vkpapps.soundbooster.utils.UserUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author VIJAY PATIDAR
 */
public class ProfileFragment extends BottomSheetDialogFragment {

    private User user;
    private ImageView userPic;
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
        userPic = view.findViewById(R.id.userPic);
        userPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), 1);
        });
        File profiles = new File(new StorageManager(requireContext()).getProfiles(), user.userId);
        if (profiles.exists()) {
            Picasso.get().load(profiles).into(userPic);
        }

        EditText editTextName = view.findViewById(R.id.userName);
        editTextName.setText(user.getName());
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            if (!name.isEmpty()) {
                user.setName(name);
                new UserUtils(v.getContext()).setUser(user);
                Toast.makeText(view.getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                onFragmentPopBackListener.onPopBackStack();
                savePic(userPic);
            } else {
                editTextName.setError("name required!");
            }
        });
    }

    private void savePic(View view) {
        File root = new StorageManager(view.getContext()).getProfiles();
        Bitmap shareBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(shareBitmap);
        view.draw(canvas);

        File f = new File(root, user.userId);
        try {
            FileOutputStream fo = new FileOutputStream(f);
            shareBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fo);
            Picasso.get().invalidate(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    userPic.setImageURI(selectedImageUri);
                }
            }
        }
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
