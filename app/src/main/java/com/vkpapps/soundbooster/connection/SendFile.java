package com.vkpapps.soundbooster.connection;

import android.util.Log;

import com.vkpapps.soundbooster.handler.FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static com.vkpapps.soundbooster.connection.ClientHelper.TAG;

public class SendFile {
    private Socket socket;
    private String path;
    private FileHandler fileHandler;

    public SendFile(Socket socket, String path, FileHandler fileHandler) {
        this.socket = socket;
        this.path = path;
        this.fileHandler = fileHandler;
    }

    public void startSending() {
        try {
            Log.d(TAG, "run: sending to client ==================================================== " + path);
            byte[] bs = new byte[1024 * 3];
            FileInputStream fis = new FileInputStream(new File(path));
            OutputStream fos = socket.getOutputStream();
            int read;
            while ((read = fis.read(bs)) > 0) {
                fos.write(bs, 0, read);
            }
            fos.flush();
            fos.close();
            fis.close();
            fileHandler.sendEmptyMessage(FileHandler.REQUEST_COMPLETED);
            Log.d(TAG, "run: sent to client ==================================================== " + path);
        } catch (IOException ex) {
            fileHandler.sendEmptyMessage(FileHandler.REQUEST_FAILED);
            ex.printStackTrace();
        }

    }
}
