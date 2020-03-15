package com.vkpapps.soundbooster.connection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_SEND = "com.vkpapps.soundbooster.action.SEND";
    public static final String ACTION_RECEIVE = "com.vkpapps.soundbooster.action.RECEIVE";
    public static final String STATUS_SUCCESS = "com.vkpapps.soundbooster.action.SUCCESS";
    public static final String STATUS_FAILED = "com.vkpapps.soundbooster.action.FAILED";

    private static final String NAME = "com.vkpapps.soundbooster.extra.NAME";
    private static final String CLIENT_ID = "com.vkpapps.soundbooster.extra.CLIENT_ID";

    private File root;

    public FileService() {
        super("FileService");
    }

    public static void startActionSend(Context context, String name, String clientId) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(NAME, name);
        intent.putExtra(CLIENT_ID, clientId);
        context.startService(intent);
    }

    public static void startActionReceive(Context context, String name, String clientId) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(ACTION_RECEIVE);
        intent.putExtra(NAME, name);
        intent.putExtra(CLIENT_ID, clientId);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        root = getDir("song", MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String name = intent.getStringExtra(NAME);
            final String clientId = intent.getStringExtra(CLIENT_ID);
            if (ACTION_SEND.equals(action)) {
                handleActionSend(name, clientId);
            } else if (ACTION_RECEIVE.equals(action)) {
                handleActionReceive(name, clientId);
            }
        }
    }

    private Socket getSocket(boolean isHost) throws IOException {
        Socket socket;
        if (isHost) {
            try (ServerSocket serverSocket = new ServerSocket(15448)) {
                serverSocket.setSoTimeout(5000);
                socket = serverSocket.accept();
            }
        } else {
            socket = new Socket();
            String host = "192.168.43.1";
            socket.connect(new InetSocketAddress(host, 15448), 5000);
        }
        return socket;
    }

    private void handleActionReceive(String name, String clientId) {
        try {
            Socket socket = getSocket(clientId != null);
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream(new File(root, name));
            byte[] bytes = new byte[2 * 1024];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            in.close();
            out.flush();
            out.close();
            socket.close();
            onSuccess(name);
        } catch (IOException e) {
            onFailed(name);
            e.printStackTrace();
        }
    }

    private void handleActionSend(String name, String clientId) {
        try {
            Socket socket = getSocket(clientId != null);
            InputStream inputStream = new FileInputStream(new File(root, name));
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = new byte[2 * 1024];
            int count;
            while ((count = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, count);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            socket.close();
            onSuccess(name);
        } catch (IOException e) {
            onFailed(name);
            e.printStackTrace();
        }
    }

    private void onSuccess(String name) {
        Intent intent = new Intent(STATUS_SUCCESS);
        intent.putExtra("name", name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onFailed(String name) {
        Intent intent = new Intent(STATUS_FAILED);
        intent.putExtra("name", name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
