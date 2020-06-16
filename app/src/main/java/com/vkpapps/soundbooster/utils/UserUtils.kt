package com.vkpapps.soundbooster.utils

import android.content.Context
import com.vkpapps.soundbooster.model.User
import java.io.*

/**
 * @author VIJAY PATIDAR
 */
class UserUtils(val context: Context) {
    fun loadUser(): User {
        try {
            val objectInputStream = ObjectInputStream(
                    FileInputStream(
                            File(StorageManager(this.context).userDir, "user")
                    )
            )
            val obj = objectInputStream.readObject()
            objectInputStream.close()
            //return user
            if (obj is User) return obj
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        // return default user
        val user = User()
        user.name = "RockStar"
        user.userId = System.currentTimeMillis().toString()
        setUser(user)
        return user
    }

    fun setUser(user: User) {
        try {
            val file = File(StorageManager(this.context).userDir, "user")
            val outputStream = ObjectOutputStream(FileOutputStream(file))
            outputStream.writeObject(user)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}