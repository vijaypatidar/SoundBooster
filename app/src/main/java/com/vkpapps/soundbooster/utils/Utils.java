package com.vkpapps.soundbooster.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.vkpapps.soundbooster.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static User getUser(File root) {
        User user = null;
        try {
            FileInputStream inputStream = new FileInputStream(new File(root, "user.txt"));
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Object object = objectInputStream.readObject();
            if (object instanceof User) {
                user = (User) object;
            }
            objectInputStream.close();
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void setUser(File root, User user) {
        try {
            FileOutputStream os = new FileOutputStream(new File(root, "user.txt"));
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(user);
            outputStream.flush();
            outputStream.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            return null;
        }
    }

    public static Socket getSocket(boolean isHost, String host) throws IOException {
        Log.d("patidar", "getSocket: ======================================== ");
        Socket socket;
        if (isHost) {
            try (ServerSocket serverSocket = new ServerSocket(15448)) {
                serverSocket.setSoTimeout(5000);
                socket = serverSocket.accept();
            }
        } else {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, 15448), 5000);
        }
        return socket;
    }

    public static void deleteFile(String path) {
        for (File file : Objects.requireNonNull(new File(path).listFiles())) {
            file.delete();
        }
    }

    public static List<File> getAllAudios(Context c) {
        List<File> files = new ArrayList<>();
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
        return files;
    }


}
