package com.vkpapps.soundbooster.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class StorageManager {
    private Context context;

    public StorageManager(Context context) {
        this.context = context;
    }


    public File getUserDir() {
        return context.getDir("userData", MODE_PRIVATE);
    }

    /*
     * @Return directory of thumbnails of song
     * */
    public File getImageDir() {
        return context.getDir("images", MODE_PRIVATE);
    }

    public File getSongDir() {
        return context.getDir("songs", MODE_PRIVATE);
    }

    public File getDownloadDir() {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    public void deleteMedia() {
        try {
            File[] songs = getSongDir().listFiles();
            if (songs != null) {
                for (File s : songs) {
                    s.delete();
                }
            }
            File[] images = getImageDir().listFiles();
            if (images != null) {
                for (File s : images) {
                    s.delete();
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void copySong(File from, OnStorageManagerListener onStorageManagerListener) {
        File to = new File(getSongDir(), from.getName().trim());
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
        File out = new File(getDownloadDir(), name);
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
}
