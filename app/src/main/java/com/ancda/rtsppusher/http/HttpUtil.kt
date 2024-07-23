package com.ancda.rtsppusher.http

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * author  : LLhon
 * date    : 2024/7/3 16:39.
 * des     :
 */
object HttpUtil {

    private val instance: OkHttpClient by lazy {
        // 初始化OkHttpClient实例，并根据需要进行配置
        OkHttpClient.Builder()
            // 在这里添加自定义配置，例如超时设置、拦截器等
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    fun getOkHttpClient(): OkHttpClient = instance
}