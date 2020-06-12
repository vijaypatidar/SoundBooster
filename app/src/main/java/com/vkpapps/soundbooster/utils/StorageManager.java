package com.vkpapps.soundbooster.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.vkpapps.soundbooster.analitics.Logger;
import com.vkpapps.soundbooster.model.AudioModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author VIJAY PATIDAR
 */

public class StorageManager {
    private Context context;
    private static List<AudioModel> audioModels = new ArrayList<>();

    public StorageManager(Context context) {
        this.context = context;
    }


    public File getUserDir() {
        return context.getDir("userData", MODE_PRIVATE);
    }

    /**
     * @Return File  private directory of thumbnails
     */
    public File getImageDir() {
        return context.getDir("images", MODE_PRIVATE);
    }

    public File getSongDir() {
        return context.getDir("songs", MODE_PRIVATE);
    }

    public File getDownloadDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    public void deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                File[] songs = dir.listFiles();
                if (songs != null) {
                    for (File s : songs) {
                        s.delete();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void copySong(File from, String name, OnStorageManagerListener onStorageManagerListener) {
        File to = new File(getSongDir(), name);
        //extract image from mp3
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(from.getAbsolutePath());
            byte[] data = mmr.getEmbeddedPicture();

            // convert the byte array to a bitmap
            if (data != null) {
                File file = new File(getImageDir(), from.getName().trim());
                if (file.createNewFile()) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyFile(from, to, onStorageManagerListener);
    }

    public void download(String name, OnStorageManagerListener onStorageManagerListener) {
        File file = new File(getSongDir(), name);
        File out = new File(getDownloadDir(), name + ".mp3");
        copyFile(file, out, onStorageManagerListener);
    }

    private void copyFile(File source, File destination, final OnStorageManagerListener onStorageManagerListener) {
        new Thread(() -> {
            try {
                FileInputStream fileInputStream = new FileInputStream(source);
                FileOutputStream fileOutputStream = new FileOutputStream(destination);
                byte[] bytes = new byte[2048];
                int read;
                while ((read = fileInputStream.read(bytes)) > 0) {
                    fileOutputStream.write(bytes, 0, read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                fileInputStream.close();

                // run on ui if context is activity
                if (onStorageManagerListener != null) {
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(() -> onStorageManagerListener.onCopyComplete(source));
                    } else {
                        // else on this thread
                        onStorageManagerListener.onCopyComplete(source);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface OnStorageManagerListener {
        void onCopyComplete(File source);
    }

    public List<AudioModel> getAllAudioFromDevice() {
        Logger.d("getAllAudioFromDevice: ");
        if (audioModels.size() == 0) {

            audioModels = new ArrayList<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST};
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

            if (c != null) {
                while (c.moveToNext()) {

                    AudioModel audioModel = new AudioModel();
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String name = c.getString(1).trim();
                    String album = c.getString(2);
                    String artist = c.getString(3);

                    audioModel.setName(name);
                    audioModel.setAlbum(album);
                    audioModel.setArtist(artist);
                    audioModel.setPath(path);
                    audioModels.add(audioModel);
                    Collections.sort(audioModels, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                }
                c.close();
            }
        }
        return audioModels;
    }
}
