package com.vkpapps.soundbooster.utils;

import com.vkpapps.soundbooster.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    public static boolean setUser(File root, User user) {
        try {
            FileOutputStream os = new FileOutputStream(new File(root, "user.txt"));
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(user);
            outputStream.flush();
            outputStream.close();
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
