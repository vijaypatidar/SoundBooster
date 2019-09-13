package com.vkpapps.soundbooster.connection;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.vkpapps.soundbooster.connection.ClientHelper.TAG;


public class ReceiveFile {
    private Socket socket;
    private String name;

    public ReceiveFile(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public void start() {
        try {
            Log.d(TAG, "run: receiving from client " + name);
            InputStream in = null;
            OutputStream out = null;
            in = socket.getInputStream();
            out = new FileOutputStream(name);
            byte[] bytes = new byte[3 * 1024];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
            socket.close();
            Log.d(TAG, "run: received from client " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
