package com.vkpapps.soundbooster.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.vkpapps.soundbooster.fragments.DashboardFragment;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.MusicPlayerFragment;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnClientControlChangeRequest;
import com.vkpapps.soundbooster.interfaces.OnCommandListener;
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener;
import com.vkpapps.soundbooster.interfaces.OnFragmentPopBackListener;
import com.vkpapps.soundbooster.interfaces.OnHostSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnLocalSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener;
import com.vkpapps.soundbooster.interfaces.OnUsersUpdateListener;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.IPManager;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.StorageManager;
import com.vkpapps.soundbooster.utils.UpdateManager;
import com.vkpapps.soundbooster.utils.Utils;
import com.vkpapps.soundbooster.view.MiniMediaController;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnLocalSongFragmentListener, OnNavigationVisibilityListener,
        OnUserListRequestListener, OnFragmentAttachStatusListener, OnClientControlChangeRequest, OnHostSongFragmentListener, SignalHandler.OnMessageHandlerListener, MusicPlayerHelper.OnMusicPlayerHelperListener,
        FileRequestReceiver.OnFileRequestReceiverListener, OnClientConnectionStateListener, OnFragmentPopBackListener,
        OnCommandListener {
    private BottomNavigationView navView;
    private ServerHelper serverHelper;
    private SignalHandler signalHandler;
    private ClientHelper clientHelper;
    private MusicPlayerHelper musicPlayer;
    private boolean isHost, initPlayer;
    private User user;
    private File root;
    private FileRequestReceiver requestReceiver;
    private NavController navController;
    private List<User> users;
    private OnUsersUpdateListener onUsersUpdateListener;
    private MiniMediaController miniMediaController;
    private HostSongFragment currentFragment;
    private ArrayList<String> queue;
    private int position = 0;
    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageManager = StorageManager.getInstance(this);
        miniMediaController = findViewById(R.id.miniController);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_local)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        users = new ArrayList<>();
        root = getDir("song", MODE_PRIVATE);
        user = Utils.loadUser(this);
        musicPlayer = new MusicPlayerHelper(this, this);
        if (user == null) {
            navController.navigate(R.id.navigation_profile);
        } else
            getChoice();

        miniMediaController.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_musicPlayer);
        });

        new UpdateManager(true).checkForUpdate(true, this);
    }

    @Override
    public void onPopBackStack() {
        navController.popBackStack();
    }


    private void getChoice() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.choice_alert_dialog, null);
        ab.setView(view);
        ab.setCancelable(false);
        AlertDialog alertDialog = ab.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        queue = new ArrayList<>();
        signalHandler = new SignalHandler(this, isHost);
        if (isHost) {
            serverHelper = new ServerHelper(signalHandler, user, this);
            serverHelper.start();
        } else {
            new Thread(() -> {
                Socket socket = new Socket();
                try {
                    IPManager ipManager = new IPManager(this);
                    String address = ipManager.hostIp();
                    Log.d("vijay", "setup: -================ " + address);
                    FileService.HOST_ADDRESS = address.substring(0, address.lastIndexOf(".") + 1) + "1";
                    socket.connect(new InetSocketAddress(FileService.HOST_ADDRESS, 1203), 5000);
                    clientHelper = new ClientHelper(socket, signalHandler, user, this);
                    clientHelper.start();
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(this);
                        ab.setTitle("No host found!");
                        ab.setMessage("There is no host on this wifi");
                        ab.setCancelable(false);
                        ab.setPositiveButton("retry", (dialog, which) -> setup(false));
                        ab.setNegativeButton("Host Party", (dialog, which) -> getChoice());
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

        queue.add(audio.getName());
        // triggered by local song fragment after copying song to private storage
        try {
            storageManager.copySong(new File(audio.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (isHost) {
            ArrayList<ClientHelper> clientHelpers = serverHelper.getClientHelpers();
            int N = clientHelpers.size() - 1;
            for (int i = 0; i <= N; i++) {
                ClientHelper chr = clientHelpers.get(i);
                String cid = chr.user.getUserId();
                FileService.startActionSend(this, audio.getName(), cid, isHost, i == N);
            }
        } else {
            sendCommand("RFR " + audio.getName());
        }
    }

    @Override
    public void onHostAudioSelected(AudioModel audioModel) {
        String command = "PLY " + audioModel.getName();
        if (isHost) {
            serverHelper.sendCommand(command);
            musicPlayer.loadAndPlay(audioModel.getName());
        } else {
            sendCommand(command);
        }
    }

    @Override
    public void onNavVisibilityChange(boolean visible) {
        if ((navView.getVisibility() == View.VISIBLE) == visible) return;
        if (visible) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.show_bottom_nav_bar);
            navView.setAnimation(animation);
            miniMediaController.setAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.hide_bottom_nav_bar);
            navView.setAnimation(animation);
            miniMediaController.setAnimation(animation);
        }
        navView.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (initPlayer)
            miniMediaController.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onPlayRequest(String name) {
        //triggered by hosted Song fragment
        musicPlayer.loadAndPlay(name);
    }

    @Override
    public void onResumeRequest() {
        musicPlayer.resume();
    }

    @Override
    public void onPauseRequest() {
        musicPlayer.pause();
    }

    @Override
    public void onSeekToRequest(int time) {
        musicPlayer.seekTo(time);
    }


    @Override
    public void broadcastCommand(String command) {
        serverHelper.sendCommand(command);
    }

    @Override
    public void onSendFileRequest(String name, String id) {
        // only host wil response this method
        if (isHost) {
            FileService.startActionSend(this, name, id, true, true);
        }
    }

    @Override
    public void onReceiveFileRequest(String name, String id) {
        // only host wil response this method
        if (isHost) {
            // prepare file receive from client
            FileService.startActionReceive(this, name, id, true);
            // prepare send request for all other client except the sender of that file
            ArrayList<ClientHelper> clientHelpers = serverHelper.getClientHelpers();
            int N = clientHelpers.size() - 1;
            for (int i = 0; i <= N; i++) {
                ClientHelper chr = clientHelpers.get(i);
                String cid = chr.user.getUserId();
                if (!cid.equals(id)) {
                    FileService.startActionSend(this, name, chr.user.getUserId(), isHost, i == N);
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
    public void onVolumeChange(float vol) {
        musicPlayer.setVolume(vol);
    }

    @Override
    public void onReceiveFileRequestAccepted(String name, String id) {
        //only client need to handle this , not for host
        // send requested file to client sent request
        FileService.startActionSend(this, name, id, isHost, false);
    }

    @Override
    public void onControlAccessChange(boolean access) {
        user.setAccess(access);
    }

    @Override
    public void onMoveToRequest(int change) {
        position += change;
        if (position >= queue.size() || position < 0) position = 0;
        if (isHost) {
            String name = queue.get(position);
            serverHelper.sendCommand("PLY " + name);
            musicPlayer.loadAndPlay(name);
        }
    }

    @Override
    public void onSongChange(String name) {
        miniMediaController.changeSong(name, root);
        position = queue.indexOf(name);
        initPlayer = true;
    }

    @Override
    public void onRequestSongNotFound(String songName) {
        String command = "SFR " + songName;
        if (!isHost) sendCommand(command);
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
    public void onRequestSuccess(String name, boolean isLastRequest) {
        if (isLastRequest) {
            // update host song list
            if (currentFragment != null) {
                currentFragment.refreshSong();
            }
        }
    }

    @Override
    public void onClientConnected(ClientHelper clientHelper) {
        users.add(clientHelper.user);
        if (onUsersUpdateListener != null) {
            runOnUiThread(() -> onUsersUpdateListener.onUserUpdated());
        }
    }

    @Override
    public void onClientDisconnected(ClientHelper clientHelper) {
        users.remove(clientHelper.user);
        if (onUsersUpdateListener != null) {
            runOnUiThread(() -> onUsersUpdateListener.onUserUpdated());
        }

        //prompt client when disconnect to a party to create or rejoin the party
        if (!isHost) {
            getChoice();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (R.id.menu_share == item.getItemId()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Sound Booster in a free android app for playing music on multiple devices simultaneously to make the sound louder" +
                    ".\nDownload the app now and make party with friends any where any time without mobile data usage. " +
                    "\nhttps://vkp.page.link/soundbooster";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sound Booster");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public List<User> onRequestUsers() {
        return users;
    }

    @Override
    public void OnClientControlChangeRequest(User user) {
        //for dashboard fragment
        if (isHost) {
            user.setAccess(!user.isAccess());
            serverHelper.sendCommandToOnly("CTR " + (user.isAccess() ? "yes" : "no"), user.getUserId());
        } else {
            Toast.makeText(this, "Only host of party can change controls of users", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        if (fragment instanceof DashboardFragment) {
            onUsersUpdateListener = (OnUsersUpdateListener) fragment;
        } else if (fragment instanceof MusicPlayerFragment) {
            miniMediaController.setEnableVisibilityChanges(false);
            musicPlayer.setPlayerChangeListener((OnMediaPlayerChangeListener) fragment);
        } else if (fragment instanceof HostSongFragment) {
            currentFragment = (HostSongFragment) fragment;
        }
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        if (fragment instanceof DashboardFragment) {
            onUsersUpdateListener = null;
        } else if (fragment instanceof MusicPlayerFragment) {
            musicPlayer.setPlayerChangeListener(null);
            miniMediaController.setEnableVisibilityChanges(true);
        } else if (fragment instanceof HostSongFragment) {
            currentFragment = null;
        }
    }

    @Override
    public void onCommandCreated(String command) {
        if (isHost) {
            serverHelper.sendCommand(command);
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("ID", user.getUserId());
            bundle.putString("command", command);
            message.setData(bundle);
            signalHandler.sendMessage(message);
        } else {
            sendCommand(command);
        }
    }

    private void sendCommand(String command) {
        if (user.isAccess()) {
            clientHelper.write(command);
        } else {
            Toast.makeText(this, "host denied", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

}
