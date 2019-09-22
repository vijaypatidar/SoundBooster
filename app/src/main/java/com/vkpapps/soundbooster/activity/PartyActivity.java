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
import android.util.Log;
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

import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.fragments.RequestFragment;
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

    private HostSongFragment hostSongFragment;
    private RequestFragment requestFragment;
    private boolean isHost;
    private Server server;
    private User user;
    private ClientHelper clientHelper;
    private SignalHandler signalHandler;
    private String root;
    private final ArrayList<String> hostSong = new ArrayList<>();
    private MusicPlayerService musicSrv;
    private TextView songTitle;
    private Intent playIntent;
    private ImageView btnPlay;

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
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
    public void handelFileRequest(Request request) {
        Toast.makeText(this, "Handle req " + request.getName() + " id " + request.getUserId(), Toast.LENGTH_SHORT).show();
        //TODO
    }


    @Override
    public void handleControl(final Control control) {
        musicSrv.processControlRequest(control);
        songTitle.setText(musicSrv.getCurrentTitle());
    }

    @Override
    public void handleRequest(InformClient informClient) {

    }


    private void sendSignal(Object s, Socket socket) {
        if (isHost) {
            server.send(s, socket);
        } else {
            clientHelper.send(s);
        }
    }

    @Override
    public void onSelectLocalMusic(LocalSong localSong) {
        //TODO remove handle control from here
        sendSignal(new Request(user.getUserId(), localSong.getName()), null);
        if (isHost) {
            Set<Map.Entry<String, Socket>> entries = Server.socketHashMap.entrySet();
            for (Map.Entry<String, Socket> entry : entries) {
                Log.d("vijay", "onSelectLocalMusic: ============= " + entry.getKey() + "   id ============ " + entry.getValue());
                Intent intent = FileService.getSendIntent(this, localSong.getName(), localSong.getPath(), entry.getKey());
                startService(intent);
            }
        } else {
            Intent intent = FileService.getSendIntent(this, localSong.getName(), localSong.getPath(), null);
            startService(intent);
        }
    }



    @Override
    public void onMusicSelectToPlay(HostSong hostSong) {
        Control control = new Control(Control.PLAY, 0, hostSong.getName());
        sendSignal(control, null);
        handleControl(control);
        Toast.makeText(this, "play " + hostSong.getName(), Toast.LENGTH_SHORT).show();
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
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

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

    private void registerMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.ACTION_PLAY);
        intentFilter.addAction(MusicPlayerService.ACTION_PAUSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isHost) {
            server.stopServer();
        } else {
            clientHelper.stopClientHelper();
        }
        Utils.deleteFile(root);
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
                startActivity(new Intent(PartyActivity.this, MusicPlayerActivity.class));
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
    }



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
}
