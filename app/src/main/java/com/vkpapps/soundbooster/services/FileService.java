package com.vkpapps.soundbooster.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vkpapps.soundbooster.connection.Server;
import com.vkpapps.soundbooster.model.InformClient;
import com.vkpapps.soundbooster.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileService extends IntentService {
    // TODO: Rename parameters
    public static final String EXTRA_FILE_NAME = "com.vkpapps.soundbooster.extra.FILE_NAME";
    public static final String EXTRA_FILE_PATH = "com.vkpapps.soundbooster.extra.FILE_PATH";
    public static final String EXTRA_CLIENT_ID = "com.vkpapps.soundbooster.extra.CLIENT_ID";
    public static final String EXTRA_HOST_ADDRESS = "com.vkpapps.soundbooster.extra.HOST_ADDRESS";
    private static final String ACTION_SEND = "com.vkpapps.soundbooster.action.SEND_FILE";
    private static final String ACTION_RECEIVE = "com.vkpapps.soundbooster.action.RECEIVE_FILE";
    private String TAG = "FileService";

    public FileService() {
        super("FileService");
    }

    public static Intent getSendIntent(Context context, String file_name, String file_path, String client_id) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(FileService.ACTION_SEND);
        intent.putExtra(FileService.EXTRA_FILE_PATH, file_path);
        intent.putExtra(FileService.EXTRA_FILE_NAME, file_name);
        intent.putExtra(FileService.EXTRA_CLIENT_ID, client_id);
        return intent;
    }

    public static Intent getReceiveIntent(Context context, String file_name, String file_path, String client_id) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(FileService.ACTION_RECEIVE);
        intent.putExtra(FileService.EXTRA_FILE_PATH, file_path);
        intent.putExtra(FileService.EXTRA_FILE_NAME, file_name);
        intent.putExtra(FileService.EXTRA_CLIENT_ID, client_id);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ============================================== called");
        if (intent != null) {
            final String action = intent.getAction();
            final String FILE_NAME = intent.getStringExtra(EXTRA_FILE_NAME);
            final String FILE_PATH = intent.getStringExtra(EXTRA_FILE_PATH);
            final String CLIENT_ID = intent.getStringExtra(EXTRA_CLIENT_ID);
            final String HOST_ADDRESS = intent.getStringExtra(EXTRA_HOST_ADDRESS);
            if (ACTION_SEND.equals(action)) {
                makeClientReady(CLIENT_ID, FILE_NAME, true);
                handleActionSend(FILE_PATH, CLIENT_ID, HOST_ADDRESS);
            } else if (ACTION_RECEIVE.equals(action)) {
                makeClientReady(CLIENT_ID, FILE_NAME, false);
                handleActionReceive(FILE_PATH, CLIENT_ID, HOST_ADDRESS);
            }
        }
    }

    private void handleActionReceive(String file_path, String client_id, String host_address) {
        Log.d(TAG, "handleActionReceive: ======================================= " + host_address + ' ' + client_id);
        try {
            Socket socket = Utils.getSocket(client_id != null, host_address);
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream(file_path);
            byte[] bytes = new byte[3 * 1024];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.flush();
            out.close();
            in.close();
            socket.close();
            //todo send success broadcast
        } catch (IOException e) {
            //todo send error broadcast
            e.printStackTrace();
        }
    }

    private void handleActionSend(String file_path, String client_id, String host_address) {
        Log.d(TAG, "handleActionSend: =============================== " + host_address + "  id " + client_id);
        try {
            Socket socket = Utils.getSocket(client_id != null, host_address);
            InputStream inputStream = new FileInputStream(new File(file_path));
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = new byte[3 * 1024];
            int count;
            while ((count = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, count);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            socket.close();
            //todo send success broadcast
        } catch (IOException e) {
            //todo send error broadcast
            e.printStackTrace();
        }
    }

    private void makeClientReady(String client_id, String file, boolean readyToReceive) {
        if (client_id != null) {
            InformClient informClient = new InformClient(readyToReceive, file);
            Socket socket = Server.socketHashMap.get(client_id);
            Server.getInstance().informClient(socket, informClient);
        }
    }
}
