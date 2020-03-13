package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.Utils;

import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity {

    private User user;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        user = new User();

        imageView = findViewById(R.id.userPic);

        final EditText editTextName = findViewById(R.id.userName);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(view -> {
            String name = editTextName.getText().toString().trim();
            if (!name.isEmpty()) {
                user.setName(name);
                user.setUserId(System.currentTimeMillis() + name.replaceAll(" ", ""));
                user.setAccess(true);
            } else {
                editTextName.setError("name required!");
            }
            Utils.setUser(user);
            startActivity(new Intent(UserDetailActivity.this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            assert data != null;
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) Objects.requireNonNull(bundle).get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}
