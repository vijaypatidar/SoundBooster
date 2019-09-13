package com.vkpapps.soundbooster.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkpapps.soundbooster.MediaPlayerService;
import com.vkpapps.soundbooster.R;
import com.vkpapps.soundbooster.adapter.LocalMusicAdapter;
import com.vkpapps.soundbooster.connection.ClientHelper;
import com.vkpapps.soundbooster.connection.FileHandler;
import com.vkpapps.soundbooster.connection.FileServer;
import com.vkpapps.soundbooster.connection.ReceiveFile;
import com.vkpapps.soundbooster.connection.SendFile;
import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.handler.SignalHandler;
import com.vkpapps.soundbooster.model.Control;
import com.vkpapps.soundbooster.model.LocalSong;
import com.vkpapps.soundbooster.model.NewSongModel;
import com.vkpapps.soundbooster.model.PlayThisSong;
import com.vkpapps.soundbooster.model.SeekModel;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PartyActivity extends AppCompatActivity implements SignalHandler.OnMessageHandlerListener, LocalMusicAdapter.OnItemClickListener {

    private String host;
    private boolean isHost;
    private Server server;
    private ClientHelper clientHelper;
    private SignalHandler signalHandler;
    private FileHandler myFileHandler;
    private MediaPlayerService mediaPlayerService;
    private String root;
    private LocalMusicAdapter localMusicAdapter;
    private ArrayList<LocalSong> localSongArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);

        root = getDir("mySong", MODE_PRIVATE).getPath();
        mediaPlayerService = MediaPlayerService.getInstance();

        signalHandler = new SignalHandler(this);

        isHost = getIntent().getBooleanExtra("isHost", false);
        host = getIntent().getStringExtra("host");

        if (isHost) {
            setUpServer();
        } else {
            setUpClient();
        }

        localSongArrayList = new ArrayList<>();
        localMusicAdapter = new LocalMusicAdapter(localSongArrayList, this);
        RecyclerView recyclerView = findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(localMusicAdapter);

        final List<File> allAudios = getAllAudios(this);
        for (int i = 0; i < allAudios.size(); i++) {
            File file = allAudios.get(i);
            localSongArrayList.add(new LocalSong(file.getPath(), file.getName()));
        }
    }

    @Override
    public void handleMessage(int what, String s) {
        Toast.makeText(PartyActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleConnectToHost() {

    }

    @Override
    public void handleSeek(final SeekModel seekModel) {
        syncDevices(seekModel);
        Toast.makeText(this, "Handle seekTo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handelNewSong(final NewSongModel newSongModel) {
        final String path = root + File.separator + newSongModel.getName();
        if (isHost) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Socket socket = FileServer.getServerSocket().accept();
                        ReceiveFile receiveFile = new ReceiveFile(socket, path);
                        receiveFile.start();
                        //send signal to receive file
                        Server.getInstance().send(newSongModel, null);
                        FileServer fileServer = new FileServer(path);
                        fileServer.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Socket socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(host, 15425), 5000);
                        ReceiveFile receiveFile = new ReceiveFile(socket, path);
                        receiveFile.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        Toast.makeText(this, "Ready to receive file " + newSongModel.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleControl(final Control control) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                switch (control.getChoice()) {
                    case Control.PLAY:
                        MediaPlayerService.getInstance().play(control.getDate());
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
            }
        }, control.getDate());

        Toast.makeText(this, "Handle control", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleSongPlay(PlayThisSong playThisSong) {
        mediaPlayerService.load(root + File.separator + playThisSong.getName());
        mediaPlayerService.play(playThisSong.getAtTime());
        Toast.makeText(this, "Handle play song " + playThisSong.getAtTime().toString(), Toast.LENGTH_SHORT).show();
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

    private void sendSignal(Object s) {
        if (isHost) {
            server.send(s, null);
        } else {
            clientHelper.send(s);
        }
    }

    private void sendFile(final String path) {
        if (isHost) {
            FileServer fileServer = new FileServer(path);
            fileServer.start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(host, 15425), 5000);
                        SendFile sendFile = new SendFile(socket, path);
                        sendFile.startSending();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void syncDevices(final SeekModel seekModel) {
        mediaPlayerService.seek(seekModel.getSeekTo(), seekModel.getDate());
    }

    public List<File> getAllAudios(Context c) {
        List<File> files = new ArrayList<>();
        if (true) {
            String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
            Cursor cursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            try {
                assert cursor != null;
                cursor.moveToFirst();
                do {
                    files.add((new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))));
                } while (cursor.moveToNext());

                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return files;
    }

    private Date getDelayForSync() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        sec = sec + 4;
        if (sec >= 60) {
            min++;
            sec = sec - 60;
            if (min >= 60) {
                hour++;
                min = min - 60;
                if (hour >= 24) {
                    hour = 0;
                }
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void onLocalMusicSelect(int position) {

        Log.d("", "onLocalMusicSelect: ============================== " + localSongArrayList.get(position).getName());
        LocalSong localSong = localSongArrayList.get(position);
//            Date date = getDelayForSync();
//            sendSignal(new PlayThisSong(allAudios.get(i).getName(), date));
//            mediaPlayerService.load(allAudios.get(i).getPath());
//            mediaPlayerService.play(date);
        ///send signal and file

        sendSignal(new NewSongModel(localSong.getName(), 8080));
        sendFile(localSong.getPath());
    }
}
