package com.ancda.rtsppusher.utils

import android.text.TextUtils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * author  : LLhon
 * date    : 2024/7/2 19:35.
 * des     :
 */
object Md5Utils {

    fun md5(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        val md5: MessageDigest
        try {
            md5 = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(string.toByteArray())
            val result = java.lang.StringBuilder()
            for (b in bytes) {
                var temp = Integer.toHexString(b.toInt() and 0xff)
                if (temp.length == 1) {
                    temp = "0$temp"
                }
                result.append(temp)
            }
            return result.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}