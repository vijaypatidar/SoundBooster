package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UserDetailActivity extends AppCompatActivity {

    private User user;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        user = new User();

        imageView = findViewById(R.id.userPic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);
            }
        });

        final EditText editTextName = findViewById(R.id.userName);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                if (!name.isEmpty()) {
                    user.setName(name);
                    user.setUserId(System.currentTimeMillis());
                } else {
                    editTextName.setError("name required!");
                }
                try {
                    File root = getDir("files", MODE_PRIVATE);
                    FileOutputStream os = new FileOutputStream(new File(root, "user.txt"));
                    ObjectOutputStream outputStream = new ObjectOutputStream(os);
                    outputStream.writeObject(user);
                    outputStream.flush();
                    outputStream.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(UserDetailActivity.this, MainActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            user.setBitmap(bitmap);
        }
    }
}
