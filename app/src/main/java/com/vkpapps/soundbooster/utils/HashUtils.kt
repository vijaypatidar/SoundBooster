package com.vkpapps.soundbooster.utils

import java.math.BigInteger
import java.security.MessageDigest

object HashUtils {
    fun getHashValue(input: ByteArray): String {
        val digest: MessageDigest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(input)
        val bigInteger = BigInteger(1, bytes)
        return bigInteger.toString(16).trim()
    }
}