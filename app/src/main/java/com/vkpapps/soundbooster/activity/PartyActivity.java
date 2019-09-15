package com.vkpapps.soundbooster.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.vkpapps.soundbooster.MediaPlayerService;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.MyFragmentPagerAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.ReceiveFile;
import com.vkpapps.soundbooster.connection.SendFile;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.fragments.HostSongFragment;
import com.vkpapps.soundbooster.fragments.LocalSongFragment;
import com.vkpapps.soundbooster.fragments.RequestFragment;
import com.vkpapps.soundbooster.handler.FileHandler;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.FileRequest;
import com.vkpapps.soundbooster.model.HostSong;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.model.LocalSong;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.Request;
import com.vkpapps.soundbooster.model.SeekModel;
import com.vkpapps.soundbooster.model.User;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static com.vkpapps.soundbooster.utils.Utils.getSocket;

public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener,
        FileHandler.OnFileHandlerListener, LocalSongFragment.OnLocalSongFragmentListener, HostSongFragment.OnHostSongFragmentListener {

    private String host;
    private HostSongFragment hostSongFragment;
    private RequestFragment requestFragment;
    private boolean isHost;
    private Server server;
    private User user;
    private ClientHelper clientHelper;
    private SignalHandler signalHandler;
    private FileHandler fileHandler;
    private MediaPlayerService mediaPlayerService;
    private String root;
    private ArrayList<FileRequest> fileRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);


        root = getDir("mySong", MODE_PRIVATE).getPath();
        isHost = getIntent().getBooleanExtra("isHost", false);
        host = getIntent().getStringExtra("host");
        fileRequests = new ArrayList<>();

        initUI();
        Utils.deleteFile(root);
        user = Utils.getUser(getDir("files", MODE_PRIVATE));
        mediaPlayerService = MediaPlayerService.getInstance();


        signalHandler = new SignalHandler(this);
        fileHandler = new FileHandler(this);


        if (isHost) {
            Toast.makeText(this, "Host of party", Toast.LENGTH_SHORT).show();
            setUpServer();
        } else {
            setUpClient();
        }
    }

    private void initUI() {
        LocalSongFragment localSongFragment = new LocalSongFragment(this, root);
        hostSongFragment = new HostSongFragment(this, root);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(hostSongFragment);
        fragments.add(localSongFragment);

        if (isHost) {
            requestFragment = new RequestFragment(fileRequests);
            fragments.add(requestFragment);
        }

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), PagerAdapter.POSITION_NONE, fragments));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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
    public void handleSeek(final SeekModel seekModel) {
        syncDevices(seekModel);
        Toast.makeText(this, "Handle seekTo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handelFileRequest(Request request) {
        Toast.makeText(this, "Handle req " + request.getName() + " id " + request.getUserId(), Toast.LENGTH_SHORT).show();
        Socket socket = null;
        boolean performAction = fileRequests.isEmpty();
        if (isHost) {
            socket = Server.socketHashMap.get(request.getUserId());
        }
        FileRequest fileRequest = new FileRequest(false, request.getName(), root + File.separator + request.getName(), socket);
        fileRequests.add(fileRequest);
        if (isHost) {
            // forward to other clients except sender
            Request newReq = new Request(user.getUserId(), request.getName());
            sendSignal(newReq, socket);
            Set<Map.Entry<String, Socket>> entries = Server.socketHashMap.entrySet();
            for (Map.Entry<String, Socket> entry : entries) {
                if (!entry.getKey().equalsIgnoreCase(request.getUserId())) {
                    fileRequest = new FileRequest(true, request.getName(), root + File.separator + request.getName(), entry.getValue());
                    fileRequests.add(fileRequest);
                    Toast.makeText(this, "for to id " + entry.getKey() + entry.getValue(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (performAction) performRequest();
    }


    @Override
    public void handleControl(final Control control) {
        switch (control.getChoice()) {
            case Control.PLAY:
                MediaPlayerService.getInstance().play(control.getTime());
                break;
            case Control.PAUSE:
                MediaPlayerService.getInstance().stop();
                break;
            case Control.NEXT:
                MediaPlayerService.getInstance().next();
                break;
            case Control.PREVIOUS:
                MediaPlayerService.getInstance().prev();
                break;
        }
        Toast.makeText(this, "Handle control", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleRequest(InformClient informClient) {
        FileRequest fileRequest = fileRequests.get(0);
        if (informClient.isReadyToReceive()) {
            receiveFile(root + File.separator + fileRequest.getName());
        } else {
            sendFile(fileRequest.getPath());
        }
    }

    @Override
    public void handleSongPlay(PlayThisSong playThisSong) {
        mediaPlayerService.load(root + File.separator + playThisSong.getName());
        mediaPlayerService.play(playThisSong.getAtTime());
        Toast.makeText(this, "Handle play song " + playThisSong.getAtTime(), Toast.LENGTH_SHORT).show();
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
        sendSignal(new Request(user.getUserId(), localSong.getName()), null);
        if (isHost) {
            boolean performAction = fileRequests.isEmpty();
            Set<Map.Entry<String, Socket>> entries = Server.socketHashMap.entrySet();
            for (Map.Entry<String, Socket> entry : entries) {
                FileRequest fileRequest = new FileRequest(true, localSong.getName(), localSong.getPath(), entry.getValue());
                fileRequests.add(fileRequest);
                Toast.makeText(this, "id " + entry.getKey() + entry.getValue(), Toast.LENGTH_SHORT).show();
            }
            if (performAction) performRequest();
        } else {
            FileRequest fileRequest = new FileRequest(true, localSong.getName(), localSong.getPath(), null);
            fileRequests.add(fileRequest);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isHost) {
            server.stopServer();
        } else {
            clientHelper.stopClientHelper();
        }
    }

    private void sendFile(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = getSocket(isHost, host);
                SendFile sendFile = new SendFile(socket, path, fileHandler);
                sendFile.startSending();
            }
        }).start();
    }

    private void receiveFile(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = getSocket(isHost, host);
                ReceiveFile receiveFile = new ReceiveFile(socket, path, fileHandler);
                receiveFile.start();
            }
        }).start();
    }

    private void syncDevices(final SeekModel seekModel) {
        mediaPlayerService.seek(seekModel.getSeekTo(), seekModel.getTime());
    }


    @Override
    public void onFileRequestCompleted() {
        fileRequests.remove(0);
        performRequest();

        try {
            hostSongFragment.onResume();
        } catch (Exception ignored) {
        }
        try {
            requestFragment.onResume();
        } catch (Exception ignored) {
        }
    }

    private void performRequest() {
        if (!fileRequests.isEmpty() && isHost) {
            FileRequest fileRequest = fileRequests.get(0);
            server.informClient(fileRequest.getSocket(), new InformClient(fileRequest.isSend()));
            if (fileRequest.isSend()) {
                sendFile(fileRequest.getPath());
            } else {
                receiveFile(root + File.separator + fileRequest.getName());
            }

        }
    }

    @Override
    public void onFileRequestFailed() {
        Toast.makeText(this, "error while preparing...", Toast.LENGTH_SHORT).show();
        fileRequests.remove(0);
        performRequest();
    }

    @Override
    public void onMusicSelectToPlay(HostSong hostSong) {
        PlayThisSong playThisSong = new PlayThisSong(hostSong.getName(), System.currentTimeMillis() + 2500);
        sendSignal(playThisSong, null);
        mediaPlayerService.load(root + File.separator + playThisSong.getName());
        mediaPlayerService.play(playThisSong.getAtTime());
        Toast.makeText(this, "play " + hostSong.getName(), Toast.LENGTH_SHORT).show();
    }
}
