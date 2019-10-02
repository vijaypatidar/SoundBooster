package com.vkpapps.soundbooster.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.ClientAdapter;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.fragments.ClientControlFragment;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.HostSong;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.model.LocalSong;
import com.vkpapps.soundbooster.model.Reaction;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.services.FileService;
import com.vkpapps.soundbooster.services.MusicPlayerService;
import com.vkpapps.soundbooster.utils.Utils;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.vkpapps.soundbooster.utils.PermissionUtils.checkStoragePermission;


public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener,
        LocalSongFragment.OnLocalSongFragmentListener, HostSongFragment.OnHostSongFragmentListener {
    private String host;
    private boolean isHost;
    private Server server;
    public static User user;
    public static ClientHelper clientHelper;
    private SignalHandler signalHandler;
    private HostSongFragment hostSongFragment;
    private String root;
    private final ArrayList<String> hostSong = new ArrayList<>();
    private MusicPlayerService musicSrv;
    private TextView songTitle;
    private ImageView btnPlay;
    private static HashMap<String, String> userHashMap = new HashMap<>();
    private LinearLayout linearLayout;
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MusicPlayerService.ACTION_PLAY:
                        btnPlay.setImageBitmap(Utils.getBitmap(PartyActivity.this, R.drawable.ic_action_pause));
                        linearLayout.setVisibility(View.VISIBLE);
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
    protected void onResume() {
        super.onResume();
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
        user = Utils.getUser(getDir("files", MODE_PRIVATE));
        root = getDir("mySong", MODE_PRIVATE).getPath();
        isHost = getIntent().getBooleanExtra("isHost", false);
        host = getIntent().getStringExtra("host");
        signalHandler = new SignalHandler(this);

        initUI();

        if (isHost) {
            Toast.makeText(this, "Host of party", Toast.LENGTH_SHORT).show();
            setUpServer();
        } else {
            setUpClient();
        }

        registerMusicReceiver();
    }

    @Override
    public void handleConnectToHost() {
        sendSignal(user);
    }


    @Override
    public void handleControl(final Control control) {
        musicSrv.processControlRequest(control);
        songTitle.setText(musicSrv.getCurrentTitle());

        // select song for move to event
        if (control.getChoice() == Control.MOVE_TO && isHost) {
            HostSong hostSong = hostSongFragment.moveTo(control.getValue());
            if (hostSong != null) onMusicSelectToPlay(hostSong);
        }
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


    private void sendSignal(Object s) {
        if (isHost) {
            server.send(s, null);
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
    public void handleReaction(Reaction data) {
        String msg = userHashMap.get(data.getUserId()) + " " + (data.isLike() ? "like " : "unlike ") + "song";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMusicSelectToPlay(HostSong hostSong) {
        Control control = new Control(Control.PLAY, 0, hostSong.getName());
        sendSignal(control);
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
            sendSignal(request);
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
        }
        return super.onOptionsItemSelected(item);
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
    public void handleNewClient(User user) {
        if (isHost) {
            //handle new client
            userHashMap.put(user.getUserId(), user.getName());
            long id = Long.parseLong(user.getUserId());
            ArrayList<User> users = ClientAdapter.users;
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                long tid = Long.parseLong(u.getUserId());
                if (id == tid) {
                    ClientAdapter.users.remove(u);
                }
            }
            ClientAdapter.users.add(user);
            Toast.makeText(this, user.getName() + " join party", Toast.LENGTH_SHORT).show();
        } else {
            // get allow sharing info
            PartyActivity.user.setSharingAllowed(user.isSharingAllowed());
        }
    }

    private void initUI() {

        ArrayList<Fragment> fragments = new ArrayList<>();
        hostSongFragment = new HostSongFragment(this, root, hostSong);
        fragments.add(hostSongFragment);

        if (checkStoragePermission(this)) {
            LocalSongFragment localSongFragment = new LocalSongFragment(this, root);
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

        songTitle = findViewById(R.id.songTitle);
        linearLayout = findViewById(R.id.ll);
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
                sendSignal(control);
                handleControl(control);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Want to close");
        builder.setMessage("Do you want to leave the party?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}
