package com.vkpapps.soundbooster.utils

import java.io.File

object FileUtils {
    fun deleteDir(dir: File) {
        try {
            val songs = dir.listFiles()
            songs?.forEach {
                it?.delete()
            }
        } catch (ignored: Exception) {
        }
    }
}