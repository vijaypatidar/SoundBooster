package com.vkpapps.soundbooster.utils;

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
        return context.getDir("image", MODE_PRIVATE);
    }

    public File getSongDir() {
        return context.getDir("song", MODE_PRIVATE);
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

    public void copySong(File from) throws IOException {
        File to = new File(getSongDir(), from.getName());
        copyFile(from, to);

        //extract image from mp3
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(to.getAbsolutePath());
            byte[] data = mmr.getEmbeddedPicture();

            // convert the byte array to a bitmap
            if (data != null) {
                File file = new File(getImageDir(), to.getName());
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

    public void download(String name) throws IOException {
        File file = new File(getSongDir(), name);
        File out = new File(getDownloadDir(), name);
        copyFile(file, out);
    }

    private void copyFile(File source, File destination) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(source);
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        byte[] bytes = new byte[1024 * 2];
        int read;
        while ((read = fileInputStream.read(bytes)) > 0) {
            fileOutputStream.write(bytes, 0, read);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        fileInputStream.close();

    }
}
