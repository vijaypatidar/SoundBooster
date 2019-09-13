package com.vkpapps.soundbooster.connection;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.vkpapps.soundbooster.connection.ClientHelper.TAG;

public class SendFile {
    private Socket socket;
    private String path;

    public SendFile(Socket socket, String path) {
        this.socket = socket;
        this.path = path;
    }

    public void startSending() {
        try {
            Log.d(TAG, "run: sending to client " + path);
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
            socket.close();
            Log.d(TAG, "run: sent to client " + path);
        } catch (IOException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
