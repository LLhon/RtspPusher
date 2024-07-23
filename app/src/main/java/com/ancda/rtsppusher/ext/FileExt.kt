package com.ancda.rtsppusher.ext

import android.os.Environment
import android.util.Log
import com.blankj.utilcode.util.Utils
import java.io.File


//获取APP缓存目录 /data/user/0/< packageName >/files
fun getAppFileDir(path: String? = ""): String = if (path.isNullOrEmpty()) {
    Utils.getApp().filesDir.absolutePath
} else {
    Utils.getApp().filesDir.absolutePath + File.separator + path
}

//日志文件目录
fun getLogImgCacheDir() = createFilePath(getAppFileDir("LogImg"))
//日志文件目录
fun getLogFileDir() = createFilePath(getAppFileDir("Log"))
//七牛缓存目录
fun getQnCacheDir() = createFilePath(getAppFileDir("qiniu"))


//获取文件名后缀
fun getFileSuffix(fileName: String?): String {
    return if (fileName.isNullOrEmpty() || fileName.lastIndexOf(".") == -1) {
        ""
    } else
        fileName.substring(fileName.lastIndexOf("."))
}

fun createFilePath(parent: String, child: String? = null): String {
    val logDir = if (child.isNullOrEmpty()) File(parent) else File(parent, child)
    if (!logDir.exists()) logDir.mkdirs()
    return logDir.absolutePath
}