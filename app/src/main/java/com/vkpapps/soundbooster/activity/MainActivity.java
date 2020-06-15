package com.vkpapps.soundbooster.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.analitics.Logger;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.ServerHelper;
import com.vkpapps.soundbooster.fragments.DashboardFragment;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.MusicPlayerFragment;
import com.vkpapps.soundbooster.interfaces.OnClientConnectionStateListener;
import com.vkpapps.soundbooster.interfaces.OnControlRequestListener;
import com.vkpapps.soundbooster.interfaces.OnFragmentAttachStatusListener;
import com.vkpapps.soundbooster.interfaces.OnFragmentPopBackListener;
import com.vkpapps.soundbooster.interfaces.OnHostSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnLocalSongFragmentListener;
import com.vkpapps.soundbooster.interfaces.OnMediaPlayerChangeListener;
import com.vkpapps.soundbooster.interfaces.OnNavigationVisibilityListener;
import com.vkpapps.soundbooster.interfaces.OnObjectCallbackListener;
import com.vkpapps.soundbooster.interfaces.OnUserListRequestListener;
import com.vkpapps.soundbooster.interfaces.OnUsersUpdateListener;
import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.model.control.ControlFile;
import com.vkpapps.soundbooster.model.control.ControlPlayer;
import com.vkpapps.soundbooster.receivers.FileRequestReceiver;
import com.vkpapps.soundbooster.service.FileService;
import com.vkpapps.soundbooster.utils.IPManager;
import com.vkpapps.soundbooster.utils.MusicPlayerHelper;
import com.vkpapps.soundbooster.utils.UpdateManager;
import com.vkpapps.soundbooster.utils.Utils;
import com.vkpapps.soundbooster.view.MiniMediaController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author VIJAY PATIDAR
 */
public class MainActivity extends AppCompatActivity implements OnLocalSongFragmentListener, OnNavigationVisibilityListener,
        OnUserListRequestListener, OnFragmentAttachStatusListener, OnHostSongFragmentListener, OnControlRequestListener, MusicPlayerHelper.OnMusicPlayerHelperListener,
        FileRequestReceiver.OnFileRequestReceiverListener, OnClientConnectionStateListener, OnFragmentPopBackListener,
        OnObjectCallbackListener {
    private BottomNavigationView navView;
    private ServerHelper serverHelper;
    private ClientHelper clientHelper;
    private MusicPlayerHelper musicPlayer;
    private boolean isHost, initPlayer;
    private User user;
    private FileRequestReceiver requestReceiver;
    private NavController navController;
    private OnUsersUpdateListener onUsersUpdateListener;
    private MiniMediaController miniMediaController;
    private HostSongFragment currentFragment;
    private ArrayList<String> queue;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_local)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        user = Utils.loadUser(this);
        musicPlayer = MusicPlayerHelper.getInstance(this, this);

        initMiniMediaPlayer();
        getChoice();

        new UpdateManager(true).checkForUpdate(true, this);
    }

    private void initMiniMediaPlayer() {
        miniMediaController = findViewById(R.id.miniController);
        miniMediaController.setOnClickListener(v -> navController.navigate(R.id.navigation_musicPlayer));
        miniMediaController.setButtonOnClick(v -> {
            onObjectCreated(new ControlPlayer(musicPlayer.isPlaying() ? ControlPlayer.ACTION_PAUSE : ControlPlayer.ACTION_RESUME, null));
        });
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
        if (isHost) {
            serverHelper = new ServerHelper(this, user, this);
            serverHelper.start();
            setupReceiver();
        } else {
            new Thread(() -> {
                Socket socket = new Socket();
                try {
                    IPManager ipManager = new IPManager(this);
                    String address = ipManager.hostIp();
                    Logger.d("setup: connection address " + address);
                    FileService.HOST_ADDRESS = address.substring(0, address.lastIndexOf(".") + 1) + "1";
                    socket.connect(new InetSocketAddress(FileService.HOST_ADDRESS, 1203), 5000);
                    clientHelper = new ClientHelper(socket, this, user, this);
                    clientHelper.start();
                    setupReceiver();
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
    }

    @Override
    public void onLocalSongSelected(AudioModel audio) {

        queue.add(audio.getName());
        // triggered by local song fragment after copying song to private storage
        if (isHost) {
            ArrayList<ClientHelper> clientHelpers = serverHelper.getClientHelpers();
            int N = clientHelpers.size() - 1;
            for (int i = 0; i <= N; i++) {
                ClientHelper chr = clientHelpers.get(i);
                FileService.startActionSend(this, audio.getName(), chr.user.getUserId(), isHost, i == N);
            }
        } else {
            sendCommand(new ControlFile(ControlFile.DOWNLOAD_REQUEST, audio.getName(), user.getUserId()));
        }
    }

    @Override
    public void onHostAudioSelected(AudioModel audioModel) {
        if (isHost) {
            serverHelper.broadcast(new ControlPlayer(ControlPlayer.ACTION_PLAY, audioModel.getName()));
            musicPlayer.loadAndPlay(audioModel.getName());
        } else {
            sendCommand(new ControlPlayer(ControlPlayer.ACTION_PLAY, audioModel.getName()));
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


    public void onUploadRequest(String name, String id) {
        // only host wil response this method
        if (isHost) {
            FileService.startActionSend(this, name, id, true, true);
        }
    }

    @Override
    public void onMusicPlayerControl(ControlPlayer controlPlayer) {
        musicPlayer.handleControl(controlPlayer);
    }

    public void onDownloadRequest(String name, String id) {
        Logger.d("onReceiveFileRequest: " + id + "==========> " + name);
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

    public void onUploadRequestAccepted(String name, String id) {
        // receiver requested file or sent by host itself
        FileService.startActionReceive(this, name, id, isHost);
    }


    public void onDownloadRequestAccepted(String name, String id) {
        //only client need to handle this , not for host
        // send requested file to client sent request
        FileService.startActionSend(this, name, id, isHost, false);
    }

    @Override
    public String getNextSong(int change) {
        if (!isHost) return null;
        position += change;
        if (position >= queue.size() || position < 0) position = 0;
        String name = queue.get(position);
        // broadcast next song information
        serverHelper.broadcast(new ControlPlayer(ControlPlayer.ACTION_PLAY, name));
        return name;
    }

    @Override
    public void onSongChange(String name) {
        runOnUiThread(() -> miniMediaController.changeSong(name));
        position = queue.indexOf(name);
        initPlayer = true;
    }

    @Override
    public void onRequestSongNotFound(String songName) {
        if (!isHost)
            sendCommand(new ControlFile(ControlFile.UPLOAD_REQUEST, songName, user.getUserId()));
    }


    @Override
    public void onRequestFailed(String name) {
        Logger.d("onRequestFailed: ==============>" + name);
    }

    @Override
    public void onRequestAccepted(String name, boolean send, String clientId) {
        Logger.d("onRequestAccepted: ============== " + name + "  " + send);
        ControlFile controlFile = new ControlFile(send ? ControlFile.UPLOAD_REQUEST_CONFIRM : ControlFile.DOWNLOAD_REQUEST_CONFIRM, name, user.getUserId());
        serverHelper.sendCommandToOnly(controlFile, clientId);
    }

    @Override
    public void onRequestSuccess(String name, boolean isLastRequest) {
        Logger.d("onRequestSuccess: " + name + "   " + isLastRequest);
        if (isLastRequest) {
            // update host song list
            if (currentFragment != null) {
                currentFragment.refreshSong();
            }
            if (isHost) {
                onLocalSongSelected(new AudioModel(name));
            }
        }
    }

    @Override
    public void onClientConnected(ClientHelper clientHelper) {
        if (onUsersUpdateListener != null && isHost) {
            runOnUiThread(() -> onUsersUpdateListener.onUserUpdated());
        }
    }

    @Override
    public void onClientDisconnected(ClientHelper clientHelper) {
        //prompt client when disconnect to a party to create or rejoin the party
        if (!isHost) {
            runOnUiThread(this::getChoice);
        } else if (onUsersUpdateListener != null) {
            runOnUiThread(() -> onUsersUpdateListener.onUserUpdated());
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
    public List<ClientHelper> onRequestUsers() {
        if (serverHelper != null) {
            return serverHelper.getClientHelpers();
        }
        return null;
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
    public void onObjectCreated(Object control) {
        if (isHost) {
            serverHelper.broadcast(control);
            if (control instanceof ControlPlayer) {
                musicPlayer.handleControl((ControlPlayer) control);
            }
        } else {
            sendCommand(control);
        }
    }

    private void sendCommand(Object command) {
        if (user.isAccess()) {
            clientHelper.write(command);
        } else {
            Toast.makeText(this, "host denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.navigation_home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (isHost) {
                    serverHelper.shutDown();
                } else {
                    clientHelper.shutDown();
                }
                musicPlayer.pause();
                finish();
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // request made by local Song fragment
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            navController.navigate(R.id.navigation_local);
        } else {
            Toast.makeText(this, "Storage permission required!", Toast.LENGTH_SHORT).show();
        }
    }
}
