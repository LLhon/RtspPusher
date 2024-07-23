package com.ancda.rtsppusher.utils

import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * author  : LLhon
 * date    : 2024/4/28 15:06.
 * des     :
 */
object UrlUtils {

    /**
     * 使用正则去除重复的端口号
     */
    fun removeDuplicatePort(url: String) : String {
        var fixUrl = url
        // 匹配URL中的端口号
        val pattern: Pattern = Pattern.compile(":(\\d+):(\\d+)")
        val matcher: Matcher = pattern.matcher(fixUrl)

        // 找到重复的端口号并替换为单个端口号
        while (matcher.find()) {
            val port1 = matcher.group(1)
            val port2 = matcher.group(2)
            if (port1 == port2) {
                fixUrl = fixUrl.replaceFirst(":$port1:$port2".toRegex(), ":$port1")
            }
        }
//        Log.d("OnvifServices", "removeDuplicatePort: url=${url}, fixUrl=${fixUrl}")
        return fixUrl
    }

    /**
     * 使用正则将多个端口号替换为最后一个端口号
     */
    fun replaceDuplicatePorts(url: String): String {
        // 匹配URL中的端口号
        var fixUrl = url
        val pattern = Pattern.compile(":(\\d+):(\\d+)")
        val matcher = pattern.matcher(fixUrl)

        // 将重复的端口号替换为最后一个端口号
        while (matcher.find()) {
            val port1 = matcher.group(1)
            val port2 = matcher.group(2)
            fixUrl = fixUrl.replace(":$port1:$port2".toRegex(), ":$port2")
        }
        return fixUrl
    }
}