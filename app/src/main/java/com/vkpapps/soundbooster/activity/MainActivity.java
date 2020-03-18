package com.vkpapps.soundbooster.activity;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.FileRequestReceiver;
import com.vkpapps.soundbooster.connection.FileService;
import com.vkpapps.soundbooster.connection.ServerHelper;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnHostSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnLocalSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnLocalSongFragmentListener, OnNavigationVisibilityListener,
        OnUserListRequestListener, OnHostSongFragmentListener, SignalHandler.OnMessageHandlerListener, MusicPlayerHelper.OnMusicPlayerHelperListener, FileRequestReceiver.OnFileRequestReceiverListener, OnClientConnectionStateListener {
    private BottomNavigationView navView;
    private ServerHelper serverHelper;
    private SignalHandler signalHandler;
    private ClientHelper clientHelper;
    private MusicPlayerHelper musicPlayer;
    private boolean isHost;
    private User user;
    private File root;
    private ArrayList<User> users = new ArrayList<>();
    private FileRequestReceiver requestReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_local)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        root = getDir("song", MODE_PRIVATE);
        user = Utils.loadUser();
        musicPlayer = new MusicPlayerHelper(this, this);
        getChoice();
    }

    private void getChoice() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.choice_alert_dialog, null);
        ab.setView(view);
        ab.setCancelable(false);
        AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        view.findViewById(R.id.btnCreateParty).setOnClickListener(v -> {
            setup(true);
            alertDialog.cancel();
        });
        view.findViewById(R.id.btnJoinParty).setOnClickListener(v -> {
            setup(false);
            alertDialog.cancel();
        });

    }

    private void setup(boolean host) {
        isHost = host;
        signalHandler = new SignalHandler(this, isHost);
        if (isHost) {
            serverHelper = new ServerHelper(signalHandler, user, this);
            serverHelper.start();
        } else {
            new Thread(() -> {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.43.1", 1203), 5000);
                    clientHelper = new ClientHelper(socket, signalHandler, user, this);
                    clientHelper.start();
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(this);
                        ab.setTitle("no host found!");
                        ab.setMessage("there is no host on this wifi");
                        ab.setCancelable(false);
                        ab.setPositiveButton("retry", (dialog, which) -> setup(false));
                        ab.create().show();
                    });
                    e.printStackTrace();
                }
            }).start();
        }
        setupReceiver();
    }

    @Override
    public void onLocalSongSelected(AudioModel audio) {

        // triggered by local song fragment after copying song to private storage
        try {
            Utils.copyFromTo(new File(audio.getPath()), new File(root, audio.getName()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (isHost) {
            for (ClientHelper chr : serverHelper.getClientHelpers()) {
                String cid = chr.user.getUserId();
                FileService.startActionSend(this, audio.getName(), cid, isHost);
            }
        } else {
            clientHelper.write("RFR " + audio.getName());
        }
    }

    @Override
    public void onHostAudioSelected(AudioModel audioModel) {
        String command = "PLY " + audioModel.getName();
        if (isHost) {
            serverHelper.sendCommand(command);
            musicPlayer.loadAndPlay(audioModel.getName());
        } else {
            //TODo check client control permission before transmitting signal
            clientHelper.write(command);
        }
    }

    @Override
    public void onNavVisibilityChange(boolean visible) {
        if (visible) {
            navView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_bottom_nav_bar));
            navView.setVisibility(View.GONE);
        } else {
            navView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.show_bottom_nav_bar));
            navView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ArrayList<User> onUserListRequest() {
        return users;
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
    public void broadcastCommand(String command) {
        serverHelper.sendCommand(command);
    }

    @Override
    public void onSendFileRequest(String name, String id) {
        // only host wil response this method
        if (isHost) {
            FileService.startActionSend(this, name, id, true);
        }
    }

    @Override
    public void onReceiveFileRequest(String name, String id) {
        // only host wil response this method
        if (isHost) {
            // prepare file receive from client
            FileService.startActionReceive(this, name, id, true);
            // prepare send request for all other client except the sender of that file
            for (ClientHelper chr : serverHelper.getClientHelpers()) {
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
        // receiver requested file or sent by host itself
        FileService.startActionReceive(this, name, id, isHost);
    }

    @Override
    public void onReceiveFileRequestAccepted(String name, String id) {
        // send requested file or client sent request
        Toast.makeText(this, "receive", Toast.LENGTH_SHORT).show();
        FileService.startActionSend(this, name, id, isHost);
    }

    @Override
    public void onSongChange(String name) {

    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Do you want to leave the party?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        builder.create().show();
    }

    @Override
    public void onRequestFailed(String name) {
        Log.d("CONTROLS", "onRequestFailed: ==============>" + name);
    }

    @Override
    public void onRequestAccepted(String name, boolean send, String clientId) {
        String command = (send ? "SFC" : "RFC") + " " + name.trim();
        Log.d("CONTROLS", "onRequestAccepted:  = " + command + "  " + clientId);
        serverHelper.sendCommandToOnly(command, clientId);
    }

    @Override
    public void onRequestSuccess(String name) {


    }

    @Override
    public void onClientConnected(ClientHelper clientHelper) {

    }

    @Override
    public void onClientDisconnected(ClientHelper clientHelper) {

    }

    private void setupReceiver() {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
        if (requestReceiver != null)
            instance.unregisterReceiver(requestReceiver);
        requestReceiver = new FileRequestReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileService.STATUS_FAILED);
        intentFilter.addAction(FileService.STATUS_SUCCESS);
        if (isHost) {
            intentFilter.addAction(FileService.REQUEST_ACCEPTED);
        }
        instance.registerReceiver(requestReceiver, intentFilter);
    }
}
