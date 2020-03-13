package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.CommandHelperRunnable;
import com.vkpapps.soundbooster.connection.ServerHelper;
import com.vkpapps.soundbooster.fragments.ClientControlFragment;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static com.vkpapps.soundbooster.utils.PermissionUtils.askStoragePermission;
import static com.vkpapps.soundbooster.utils.PermissionUtils.checkStoragePermission;


public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener {
    private String host;
    private boolean isHost;
    public static User user;
    private ServerHelper serverHelper;
    private SignalHandler signalHandler;
    private CommandHelperRunnable commandHelperRunnable;
    //fragments
    ClientControlFragment clientControlFragment;
    private MusicPlayerHelper musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, "ca-app-pub-4043007075380826~2360517416");
        user = Utils.loadUser();
        isHost = getIntent().getBooleanExtra("isHost", false);
        setup();
        initUI();

        musicPlayer = MusicPlayerHelper.getInstance(this);

    }

    private void setup() {
        signalHandler = new SignalHandler(this, isHost);
        if (isHost) {
            serverHelper = new ServerHelper(signalHandler);
            new Thread(serverHelper).start();
        } else {
            new Thread(() -> {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.43.1", 1203));
                    commandHelperRunnable = new CommandHelperRunnable(socket, signalHandler);
                    new Thread(commandHelperRunnable).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
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
        } else if (id == R.id.sync_time) {
            String command = "ST 10000";
            if (isHost) {
                serverHelper.sendCommand(command);
                musicPlayer.seekTo(10000);
            } else {
                commandHelperRunnable.write(command);
            }
        }
        return super.onOptionsItemSelected(item);
    }



    private void initUI() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        if (!checkStoragePermission(this)) askStoragePermission(this);

        LocalSongFragment localSongFragment = new LocalSongFragment();
        fragments.add(localSongFragment);
        HostSongFragment hostSongFragment = new HostSongFragment();
        fragments.add(hostSongFragment);

        if (isHost) {
            clientControlFragment = new ClientControlFragment();
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
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        builder.create().show();
    }

    @Override
    public void onPlayRequest(String name) {
        musicPlayer.loadAndPlay(name);
        Toast.makeText(this, "onPlayRequest " + name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResumeRequest() {
        musicPlayer.resume();
        Toast.makeText(this, "onResumeRequest", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPauseRequest() {
        musicPlayer.pause();
        Toast.makeText(this, "onPauseRequest ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSeekToRequest(int time) {
        musicPlayer.seekTo(time);
        Toast.makeText(this, "onSeekToRequest " + time, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIdentityRequest(String user, String id) {
        String[] strings = user.split(",");
        User tmp = new User(strings[0], strings[1]);
        serverHelper.setUser(tmp, id);
        clientControlFragment.addUser(tmp);
        Toast.makeText(this, "onIdentityRequest " + user, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void broadcastCommand(String command) {
        serverHelper.sendCommand(command);
    }
}
