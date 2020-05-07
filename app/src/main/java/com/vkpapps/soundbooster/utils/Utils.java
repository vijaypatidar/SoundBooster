package com.vkpapps.soundbooster.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.vkpapps.soundbooster.model.AudioModel;
import com.vkpapps.soundbooster.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static File root;
    public static File imageRoot;

    public static User loadUser() {
        User user = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File(root, "user")));
            Object object = objectInputStream.readObject();
            if (object instanceof User) {
                user = (User) object;
            }
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void setUser(User user) {
        try {
            File file = new File(root, "user");
            if (!file.exists()) file.canExecute();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(user);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static List<AudioModel> getAllAudioFromDevice(final Context context) {
        Log.d("control", "getAllAudioFromDevice: ========================");
        final List<AudioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {

                AudioModel audioModel = new AudioModel();
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String album = c.getString(1);
                String artist = c.getString(2);
                String name = new File(path).getName();

                audioModel.setName(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);

                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }
//        Log.d("control", "getAllAudioFromDevice:  ================= " + tempAudioList.size());
        return tempAudioList;
    }

    public static void copyFromTo(File from, File to) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(from.getPath());
        FileOutputStream fileOutputStream = new FileOutputStream(to);
        byte[] bytes = new byte[1024 * 2];
        int read;
        while ((read = fileInputStream.read(bytes)) > 0) {
            fileOutputStream.write(bytes, 0, read);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        fileInputStream.close();

        //extract image from mp3
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(to.getAbsolutePath());
            byte[] data = mmr.getEmbeddedPicture();

            // convert the byte array to a bitmap
            if (data != null) {
                File file = new File(imageRoot, to.getName());
                if (file.createNewFile()) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
