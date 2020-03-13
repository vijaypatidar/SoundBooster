package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.utils.PermissionUtils;


public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private TextView message;
    private SignalHandler signalHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionUtils.checkStoragePermission(this)) {
            PermissionUtils.askStoragePermission(this);
        }

        intent = new Intent(this, PartyActivity.class);

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("what you want to be...");
        ab.setPositiveButton("host", (dialog, which) -> {
            createHost();
        });
        ab.setNegativeButton("member", (dialog, which) -> {
            connectToHost();
        });
        ab.create().show();
    }

    private void createHost() {
        Toast.makeText(this, "host", Toast.LENGTH_SHORT).show();
        intent.putExtra("isHost", true);
        startActivity(intent);
        finish();
    }

    private void connectToHost() {
            intent.putExtra("isHost", false);
        startActivity(intent);
        finish();
    }
}
