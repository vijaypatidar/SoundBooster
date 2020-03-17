package com.vkpapps.soundbooster.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.CommandHelperRunnable;
import com.vkpapps.soundbooster.connection.FileRequestReceiver;
import com.vkpapps.soundbooster.connection.FileService;
import com.vkpapps.soundbooster.connection.ServerHelper;
import com.vkpapps.soundbooster.fragments.ClientControlFragment;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static com.vkpapps.soundbooster.utils.PermissionUtils.askStoragePermission;
import static com.vkpapps.soundbooster.utils.PermissionUtils.checkStoragePermission;


public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener,
        MusicPlayerHelper.OnMusicPlayerHelperListener
        , HostSongFragment.OnHostSongFragmentListener, LocalSongFragment.OnLocalSongFragmentListener, FileRequestReceiver.OnFileRequestReceiverListener {
    private TextView audioTitle;
    private LinearLayout linearLayout;
    private boolean isHost;
    public static User user;
    private ServerHelper serverHelper;
    private SignalHandler signalHandler;
    private CommandHelperRunnable commandHelperRunnable;
    //fragments
    ClientControlFragment clientControlFragment;
    HostSongFragment hostSongFragment;
    private MusicPlayerHelper musicPlayer;
    private FileRequestReceiver requestReceiver;
    private File root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, "ca-app-pub-4043007075380826~2360517416");
        musicPlayer = MusicPlayerHelper.getInstance(this, this);
        user = Utils.loadUser();
        root = getDir("song", MODE_PRIVATE);
        isHost = getIntent().getBooleanExtra("isHost", false);
        setup();
        initUI();
        setupReceiver();
    }

    private void setupReceiver() {
        requestReceiver = new FileRequestReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileService.STATUS_FAILED);
        intentFilter.addAction(FileService.STATUS_SUCCESS);
        if (isHost) {
            intentFilter.addAction(FileService.REQUEST_ACCEPTED);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(requestReceiver, intentFilter);
    }

    private void setup() {
        signalHandler = new SignalHandler(this, isHost);
        if (isHost) {
            serverHelper = new ServerHelper(signalHandler, user);
            new Thread(serverHelper).start();
        } else {
            new Thread(() -> {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.43.1", 1203), 5000);
                    commandHelperRunnable = new CommandHelperRunnable(socket, signalHandler, user);
                    new Thread(commandHelperRunnable).start();
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setTitle("no host found!");
                        ab.setMessage("there is no host on this wifi");
                        ab.setCancelable(false);
                        ab.setPositiveButton("retry", (dialog, which) -> setup());
                        ab.create().show();
                    });
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

        LocalSongFragment localSongFragment = new LocalSongFragment(this);
        fragments.add(localSongFragment);
        hostSongFragment = new HostSongFragment(this);
        fragments.add(hostSongFragment);

        if (isHost) {
            clientControlFragment = new ClientControlFragment(serverHelper);
            fragments.add(clientControlFragment);
        }

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), PagerAdapter.POSITION_NONE, fragments));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        linearLayout = findViewById(R.id.ll);
        audioTitle = findViewById(R.id.audio_title);
        findViewById(R.id.btnPlayPause).setOnClickListener(v -> {
            musicPlayer.pause();
        });
        audioTitle.setOnClickListener(v -> {

        });
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
        //triggered by hosted Song fragment
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
    public void onNewDeviceConnected(String id) {
        Toast.makeText(this, "onNewDeviceConnected " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceDisconnected(String id) {
        Toast.makeText(this, "onDeviceDisconnected " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendFileRequest(String name, String id) {
        if (isHost)
            FileService.startActionSend(this, name, id, true);
    }

    @Override
    public void onReceiveFileRequest(String name, String id) {
        if (isHost) {
            FileService.startActionReceive(this, name, id, true);
            for (CommandHelperRunnable chr : serverHelper.getCommandHelperRunnables()) {
                String cid = chr.user.getUserId();
                if (!cid.equals(id)) {
                    FileService.startActionSend(this, name, chr.user.getUserId(), isHost);
                    Toast.makeText(this, "onReceiveFileRequest " + name, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onSendFileRequestAccepted(String name, String id) {
        // receiver requested file
        FileService.startActionReceive(this, name, id, isHost);
        Toast.makeText(this, name + "send", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveFileRequestAccepted(String name, String id) {
        // send request file
        Toast.makeText(this, "receive", Toast.LENGTH_SHORT).show();
        FileService.startActionSend(this, name, id, isHost);
    }



    @Override
    public void broadcastCommand(String command) {
        serverHelper.sendCommand(command);
    }

    @Override
    public void onSongChange(String name) {
        runOnUiThread(() -> {
            audioTitle.setText(name);
            linearLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onSelectAudio(AudioModel audioModel) {

        String command = "PLY " + audioModel.getName();
        if (isHost) {
            serverHelper.sendCommand(command);
            musicPlayer.loadAndPlay(audioModel.getName());
        } else {
            commandHelperRunnable.write(command);
        }
    }

    @Override
    public void onLocalSongSelected(AudioModel audioMode) {

        // triggered by local song fragment after copying song to private storage
        try {
            Utils.copyFromTo(new File(audioMode.getPath()), new File(root, audioMode.getName().trim()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (isHost) {
            for (CommandHelperRunnable chr : serverHelper.getCommandHelperRunnables()) {
                String cid = chr.user.getUserId();
                FileService.startActionSend(this, audioMode.getName(), cid, isHost);
            }
        } else {
            commandHelperRunnable.write("RFR " + audioMode.getName());
        }

    }

    @Override
    public void onRequestFailed(String name) {

    }

    @Override
    public void onRequestAccepted(String name, boolean send, String clientId) {
        String command = (send ? "SFC" : "RFC") + " " + name.trim();
        Log.d("CONTROLS", "onRequestAccepted:  = " + command + "  " + clientId);
        serverHelper.sendCommandToOnly(command, clientId);
    }

    @Override
    public void onRequestSuccess(String name) {
        hostSongFragment.refreshSong();
    }
}
