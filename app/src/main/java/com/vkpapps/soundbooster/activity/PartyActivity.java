package com.vkpapps.soundbooster.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.HostSong;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.model.LocalSong;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.services.FileService;
import com.vkpapps.soundbooster.services.MusicPlayerService;
import com.vkpapps.soundbooster.utils.Utils;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static com.vkpapps.soundbooster.utils.PermissionUtils.askStoragePermission;
import static com.vkpapps.soundbooster.utils.PermissionUtils.checkStoragePermission;


public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener,
        LocalSongFragment.OnLocalSongFragmentListener, HostSongFragment.OnHostSongFragmentListener {
    private String host;
    private boolean isHost;
    private Server server;
    private User user;
    public static ClientHelper clientHelper;
    private SignalHandler signalHandler;
    private HostSongFragment hostSongFragment;
    private String root;
    private AdView mAdView;
    private final ArrayList<String> hostSong = new ArrayList<>();
    private MusicPlayerService musicSrv;
    private TextView songTitle;
    private ImageView btnPlay;
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
    public void handleNewClient(User user) {
        Toast.makeText(this, user.getName() + " join party", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleConnectToHost() {
        sendSignal(user, null);
    }


    @Override
    public void handleControl(final Control control) {
        musicSrv.processControlRequest(control);
        songTitle.setText(musicSrv.getCurrentTitle());
    }

    @Override
    public void handelClientFileRequest(Request request) {
        Intent intent = FileService.getReceiveIntent(this, request.getName(), request.getUserId());
        startService(intent);
        Set<Map.Entry<String, Socket>> entries = Server.socketHashMap.entrySet();
        long id = Long.parseLong(request.getUserId());
        for (Map.Entry<String, Socket> entry : entries) {
            long tid = Long.parseLong(entry.getKey());
            if (id != tid) {
                Intent sendIntent = FileService.getSendIntent(this, request.getName(), entry.getKey());
                startService(sendIntent);
            }
        }
    }


    private void sendSignal(Object s, Socket socket) {
        if (isHost) {
            server.send(s, socket);
        } else {
            clientHelper.send(s);
        }
    }

    @Override
    public void handleRequest(InformClient informClient) {
        Intent intent;
        if (informClient.isReadyToReceive()) {
            intent = FileService.getReceiveIntent(this, informClient.getFileName(), null);
        } else {
            intent = FileService.getSendIntent(this, informClient.getFileName(), null);
        }
        intent.putExtra(FileService.EXTRA_HOST_ADDRESS, host);
        startService(intent);
    }


    @Override
    public void onMusicSelectToPlay(HostSong hostSong) {
        Control control = new Control(Control.PLAY, 0, hostSong.getName());
        sendSignal(control, null);
        handleControl(control);
    }


    @Override
    public void onSelectLocalMusic(LocalSong localSong) {
        if (isHost) {
            Set<Map.Entry<String, Socket>> entries = Server.socketHashMap.entrySet();
            for (Map.Entry<String, Socket> entry : entries) {
                Intent intent = FileService.getSendIntent(this, localSong.getName(), entry.getKey());
                startService(intent);
            }
        } else {
            Request request = new Request(user.getUserId(), localSong.getName());
            sendSignal(request, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isHost) {
            server.stopServer();
        } else {
            clientHelper.stopClientHelper();
        }
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MusicPlayerService.ACTION_PLAY:
                        btnPlay.setImageBitmap(Utils.getBitmap(PartyActivity.this, R.drawable.ic_action_pause));
                        break;
                    case MusicPlayerService.ACTION_PAUSE:
                        btnPlay.setImageBitmap(Utils.getBitmap(PartyActivity.this, R.drawable.ic_action_play));
                        break;
                    case FileService.FILE_SENT_SUCCESS:
                    case FileService.FILE_RECEIVED_SUCCESS:
                        hostSongFragment.refreshList();
                        break;
                    case FileService.FILE_SENDING_FAILED:
                        Toast.makeText(context, "sending failed", Toast.LENGTH_SHORT).show();
                        break;
                    case FileService.FILE_RECEIVING_FAILED:
                        Toast.makeText(context, "receiving failed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }

    private void setUpServer() {
        server = Server.getInstance();
        server.setSignalHandler(signalHandler);
        server.start();
    }

    private void setUpClient() {
        clientHelper = new ClientHelper(host, signalHandler);
        clientHelper.start();
    }

    private void doBindService() {
        if (musicSrv == null)
            bindService(new Intent(this, MusicPlayerService.class), musicConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        MobileAds.initialize(this, "ca-app-pub-4043007075380826~2360517416");
        root = getDir("mySong", MODE_PRIVATE).getPath();
        isHost = getIntent().getBooleanExtra("isHost", false);
        host = getIntent().getStringExtra("host");

        if (checkStoragePermission(this)) {
            initUI();
        } else {
            askStoragePermission(this);
        }

        user = Utils.getUser(getDir("files", MODE_PRIVATE));

        signalHandler = new SignalHandler(this);

        if (isHost) {
            Toast.makeText(this, "Host of party", Toast.LENGTH_SHORT).show();
            setUpServer();
        } else {
            setUpClient();
        }

        registerMusicReceiver();
    }

    private void initUI() {
        LocalSongFragment localSongFragment = new LocalSongFragment(this, root);
        hostSongFragment = new HostSongFragment(this, root, hostSong);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(hostSongFragment);
        fragments.add(localSongFragment);


        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), PagerAdapter.POSITION_NONE, fragments));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        songTitle = findViewById(R.id.songTitle);
        songTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PartyActivity.this, MusicPlayerActivity.class);
                intent.putExtra("isHost", isHost);
                if (!songTitle.getText().toString().isEmpty()) {
                    startActivity(intent);
                }
            }
        });

        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Control control = new Control(musicSrv.isPlaying() ? Control.PAUSE : Control.PLAY, 0, null);
                sendSignal(control, null);
                handleControl(control);
            }
        });

        mAdView = findViewById(R.id.adView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void registerMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.ACTION_PLAY);
        intentFilter.addAction(MusicPlayerService.ACTION_PAUSE);
        intentFilter.addAction(FileService.FILE_RECEIVED_SUCCESS);
        intentFilter.addAction(FileService.FILE_RECEIVING_FAILED);
        intentFilter.addAction(FileService.FILE_SENDING_FAILED);
        intentFilter.addAction(FileService.FILE_SENT_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
    }
}
