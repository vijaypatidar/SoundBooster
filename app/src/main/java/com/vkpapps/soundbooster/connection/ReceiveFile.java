package com.vkpapps.soundbooster.connection;

import android.util.Log;

import com.vkpapps.soundbooster.handler.FileHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.vkpapps.soundbooster.connection.ClientHelper.TAG;


public class ReceiveFile {
    private Socket socket;
    private String path;
    private FileHandler fileHandler;

    public ReceiveFile(Socket socket, String path, FileHandler fileHandler) {
        this.socket = socket;
        this.path = path;
        this.fileHandler = fileHandler;
    }

    public void start() {
        try {
            Log.d(TAG, "run: receiving from client ==================================================== " + path);
            InputStream in = socket.getInputStream();
            OutputStream out = new FileOutputStream(path);
            byte[] bytes = new byte[3 * 1024];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
                Log.d(TAG, "receiving from client ==================================================== " + count);
            }
            out.flush();
            out.close();
            in.close();
            socket.close();
            fileHandler.sendEmptyMessage(FileHandler.REQUEST_COMPLETED);

            Log.d(TAG, "run: received from client ==================================================== " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
