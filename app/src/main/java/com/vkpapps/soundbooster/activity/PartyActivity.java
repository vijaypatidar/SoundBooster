package com.vkpapps.soundbooster.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.fragments.ClientControlFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.services.MusicPlayerService;
import com.vkpapps.soundbooster.utils.Utils;

import java.util.ArrayList;

import static com.vkpapps.soundbooster.utils.FirebaseUtils.getAdRequest;
import static com.vkpapps.soundbooster.utils.PermissionUtils.checkStoragePermission;


public class PartyActivity extends AppCompatActivity {
    private String host;
    private boolean isHost;
    public static User user;

    private MusicPlayerService musicSrv;

    @Override
    protected void onResume() {
        super.onResume();
        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(getAdRequest());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO close socket here
    }

    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicSrv = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, "ca-app-pub-4043007075380826~2360517416");
        user = Utils.loadUser(getDir("files", MODE_PRIVATE));
        isHost = getIntent().getBooleanExtra("isHost", false);
        host = getIntent().getStringExtra("host");

        initUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } else if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Sound Booster in a free android app for playing music on multiple devices simultaneously to make the sound louder" +
                    ".\nDownload the app now and make party with friends any where any time without data usage. " +
                    "\nhttps://vkp.page.link/soundbooster";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sound Booster");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }



    private void doBindService() {
        if (musicSrv == null)
            bindService(new Intent(this, MusicPlayerService.class), musicConnection, BIND_AUTO_CREATE);
    }

    private void initUI() {

        ArrayList<Fragment> fragments = new ArrayList<>();

        if (checkStoragePermission(this)) {
            LocalSongFragment localSongFragment = new LocalSongFragment();
            fragments.add(localSongFragment);
        }

        if (isHost) {
            ClientControlFragment clientControlFragment = new ClientControlFragment();
            fragments.add(clientControlFragment);
        }
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), PagerAdapter.POSITION_NONE, fragments));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to leave the party?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}
