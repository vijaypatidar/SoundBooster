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
import java.util.Calendar;
import java.util.Date;
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
    public static Socket getSocket(boolean isHost, String host) {
        Socket socket = null;


        if (isHost) {
            try {
                Log.d("patidar", "getSocket: ser ======================================== ");
                ServerSocket serverSocket;
                serverSocket = new ServerSocket(15448);
                socket = serverSocket.accept();
                Log.d("patidar", "getSocket: found  ======================================== ");
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.d("patidar", "getSocket: con ======================================== ");
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, 15448), 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public static Date getDelayForSync() {
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
}
