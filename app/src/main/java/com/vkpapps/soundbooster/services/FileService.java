package com.vkpapps.soundbooster.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
    public static final String EXTRA_FILE_NAME = "com.vkpapps.soundbooster.extra.FILE_NAME";
    public static final String EXTRA_CLIENT_ID = "com.vkpapps.soundbooster.extra.CLIENT_ID";
    public static final String EXTRA_HOST_ADDRESS = "com.vkpapps.soundbooster.extra.HOST_ADDRESS";

    private static final String ACTION_SEND = "com.vkpapps.soundbooster.action.SEND_FILE";
    private static final String ACTION_RECEIVE = "com.vkpapps.soundbooster.action.RECEIVE_FILE";

    //for broadcast uses
    public static final String FILE_SENT_SUCCESS = "com.vkpapps.soundbooster.FILE_SENT";
    public static final String FILE_SENDING_FAILED = "com.vkpapps.soundbooster.FILE_SENDING_FAILED";
    public static final String FILE_RECEIVED_SUCCESS = "com.vkpapps.soundbooster.FILE_RECEIVED";
    public static final String FILE_RECEIVING_FAILED = "com.vkpapps.soundbooster.FILE_RECEIVING_FAILED";

    private File root;


    public FileService() {
        super("FileService");
    }

    public static Intent getSendIntent(Context context, String file_name, String client_id) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(FileService.ACTION_SEND);
        intent.putExtra(FileService.EXTRA_FILE_NAME, file_name);
        intent.putExtra(FileService.EXTRA_CLIENT_ID, client_id);
        return intent;
    }

    public static Intent getReceiveIntent(Context context, String file_name, String client_id) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(FileService.ACTION_RECEIVE);
        intent.putExtra(FileService.EXTRA_FILE_NAME, file_name);
        intent.putExtra(FileService.EXTRA_CLIENT_ID, client_id);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        root = getDir("mySong", MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String FILE_NAME = intent.getStringExtra(EXTRA_FILE_NAME);
            final String CLIENT_ID = intent.getStringExtra(EXTRA_CLIENT_ID);
            final String HOST_ADDRESS = intent.getStringExtra(EXTRA_HOST_ADDRESS);
            if (ACTION_SEND.equals(action)) {
                makeClientReady(CLIENT_ID, FILE_NAME, true);
                handleActionSend(FILE_NAME, CLIENT_ID, HOST_ADDRESS);
            } else if (ACTION_RECEIVE.equals(action)) {
                makeClientReady(CLIENT_ID, FILE_NAME, false);
                handleActionReceive(FILE_NAME, CLIENT_ID, HOST_ADDRESS);
            }
        }
    }

    private void handleActionReceive(String file_name, String client_id, String host_address) {
        try {
            Socket socket = Utils.getSocket(client_id != null, host_address);
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream(new File(root, file_name));
            byte[] bytes = new byte[3 * 1024];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.flush();
            out.close();
            in.close();
            socket.close();
            sendBroadcastFileReceived(file_name);
        } catch (IOException e) {
            sendBroadcastFileReceivingFailed(file_name);
            e.printStackTrace();
        }
    }

    private void handleActionSend(String file_name, String client_id, String host_address) {
        try {
            Socket socket = Utils.getSocket(client_id != null, host_address);
            InputStream inputStream = new FileInputStream(new File(root, file_name));
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
            sendBroadcastFileSent(file_name);
        } catch (IOException e) {
            sendBroadcastFileSendingFailed(file_name);
            e.printStackTrace();
        }
    }

    private void sendBroadcastFileSent(String fileName) {
        Intent intent = new Intent(FILE_SENT_SUCCESS);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastFileSendingFailed(String fileName) {
        Intent intent = new Intent(FILE_SENDING_FAILED);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastFileReceived(String fileName) {
        Intent intent = new Intent(FILE_RECEIVED_SUCCESS);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastFileReceivingFailed(String fileName) {
        Intent intent = new Intent(FILE_RECEIVING_FAILED);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void makeClientReady(String client_id, String file_name, boolean readyToReceive) {
        if (client_id != null) {
            InformClient informClient = new InformClient(readyToReceive, file_name);
            Socket socket = Server.socketHashMap.get(client_id);
            Server.getInstance().informClient(socket, informClient);
        }
    }
}
