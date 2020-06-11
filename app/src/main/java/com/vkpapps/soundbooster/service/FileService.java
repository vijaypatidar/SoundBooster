package com.vkpapps.soundbooster.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

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
    public static final String REQUEST_ACCEPTED = "com.vkpapps.soundbooster.action.ACCEPTED";

    public static final String NAME = "com.vkpapps.soundbooster.extra.NAME";
    public static final String CLIENT_ID = "com.vkpapps.soundbooster.extra.CLIENT_ID";
    private static final String IS_HOST = "com.vkpapps.soundbooster.extra.IS_HOST";
    public static final String LAST_REQUEST = "com.vkpapps.soundbooster.action.IS_LAST_REQUEST";
    public static String HOST_ADDRESS;
    private File root;
    private File imageRoot;

    public FileService() {
        super("FileService");
    }

    public static void startActionSend(Context context, String name, String clientId, boolean isHost, boolean isLast) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(NAME, name);
        intent.putExtra(CLIENT_ID, clientId);
        intent.putExtra(IS_HOST, isHost);
        intent.putExtra(LAST_REQUEST, isLast);
        context.startService(intent);
    }

    public static void startActionReceive(Context context, String name, String clientId, boolean isHost) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(ACTION_RECEIVE);
        intent.putExtra(NAME, name);
        intent.putExtra(CLIENT_ID, clientId);
        intent.putExtra(IS_HOST, isHost);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        root = getDir("song", MODE_PRIVATE);
        imageRoot = getDir("image", MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String name = intent.getStringExtra(NAME);
            final String clientId = intent.getStringExtra(CLIENT_ID);
            final boolean isHost = intent.getBooleanExtra(IS_HOST, false);
            if (ACTION_SEND.equals(action)) {
                handleActionSend(name, clientId, isHost);
            } else if (ACTION_RECEIVE.equals(action)) {
                handleActionReceive(name, clientId, isHost);
            }
        }
    }

    private Socket getSocket(boolean isHost) throws IOException {
        Socket socket;
        if (isHost) {
            try (ServerSocket serverSocket = new ServerSocket(15448)) {
                serverSocket.setSoTimeout(4000);
                socket = serverSocket.accept();
            }
        } else {
            socket = new Socket();
            socket.connect(new InetSocketAddress(HOST_ADDRESS, 15448), 3000);
        }
        return socket;
    }

    private void handleActionReceive(String name, String clientId, boolean isHost) {
        try {
            onAccepted(name, clientId, false);
            Socket socket = getSocket(isHost);
            InputStream in = socket.getInputStream();
            File file = new File(root, name.trim());
            OutputStream out = new FileOutputStream(file);
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
            saveCover(name);
        } catch (IOException e) {
            onFailed(name);
            e.printStackTrace();
        }
    }

    private void handleActionSend(String name, String clientId, boolean isHost) {
        try {
            onAccepted(name, clientId, true);
            Socket socket = getSocket(isHost);
            File file = new File(root, name.trim());
            InputStream inputStream = new FileInputStream(file);
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
        intent.putExtra(NAME, name);
        Log.d("CONTROLS", "onSuccess: ============ " + name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onFailed(String name) {
        Intent intent = new Intent(STATUS_FAILED);
        intent.putExtra(NAME, name);
        Log.d("CONTROLS", "onFailed: ============ " + name);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void onAccepted(String name, String clientID, boolean send) {
        Intent intent = new Intent(REQUEST_ACCEPTED);
        intent.putExtra(NAME, name);
        intent.putExtra(ACTION_SEND, send);
        intent.putExtra(CLIENT_ID, clientID);
        Log.d("CONTROLS", "onAccepted: ========== " + name + "   " + send);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveCover(String name) {
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(new File(root, name).getAbsolutePath());

            byte[] data = mmr.getEmbeddedPicture();

            // convert the byte array to a bitmap
            if (data != null) {
                File file = new File(imageRoot, name);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
