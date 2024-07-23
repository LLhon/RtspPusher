package com.ancda.rtsppusher.http.interceptor

import com.ancda.rtsppusher.BuildConfig
import com.blankj.utilcode.util.DeviceUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 公共头部参数拦截器，传入heads 公开
 */
class CommonInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("Accept-Language", "zh_CN")
            .addHeader("Version", BuildConfig.VERSION_NAME)
            .addHeader("Bid", "")
            .addHeader("Device-Identity", DeviceUtils.getUniqueDeviceId())
            .addHeader("Device-Name", DeviceUtils.getModel())
            .addHeader("Device-Model", "${DeviceUtils.getManufacturer()}/${DeviceUtils.getModel()}")
            .addHeader("OS", "Android")
        return chain.proceed(builder.build())
    }
}