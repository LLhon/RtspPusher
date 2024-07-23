package com.ancda.rtsppusher.http

import androidx.annotation.IntDef
import com.ancda.rtsppusher.BuildConfig
import com.ancda.rtsppusher.data.LoginResponse
import com.ancda.rtsppusher.data.QiniuResponse
import libcore.net.okhttp.OkDomain
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 网络请求API
 */
interface ApiService {
    companion object {
        const val DOMAIN_RELEASE = 0 // 正式
        const val DOMAIN_TEST = 1 // 测试
        const val DOMAIN_BETA = 2 // 预发布
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(DOMAIN_RELEASE, DOMAIN_TEST, DOMAIN_BETA)
        annotation class DomainType

        // 正式域名
        var currentDoMain = if (BuildConfig.DEBUG) DOMAIN_TEST else DOMAIN_RELEASE
        private const val DOMAIN_API = "DOMAIN_API"
        private const val DOMAIN_H5 = "DOMAIN_H5"
        //正式
        private const val DOMAIN_URL_API = ""
        private const val DOMAIN_URL_H5 = ""
        //预发布
        private const val DOMAIN_URL_API_BETA = ""
        private const val DOMAIN_URL_H5_BETA = ""
        //测试
        private const val DOMAIN_URL_API_TEST = ""
        private const val DOMAIN_URL_H5_TEST = ""
        //mqtt扫码打卡
        private const val DOMAIN_MQTT_URL_TEST = "https://test-dev.ancda.com/"
        private const val DOMAIN_MQTT_URL = "https://dev.ancda.com/"
        private const val DOMAIN_MQTT = "DOMAIN_MQTT"

        // 切换域名  0正式  1测试  2预发布
        fun setDomain(@DomainType type: Int) {
            currentDoMain = type
            OkDomain.setMainDomain(if (type == DOMAIN_RELEASE) DOMAIN_URL_API else if (type == DOMAIN_TEST) DOMAIN_URL_API_TEST else DOMAIN_URL_API_BETA)
            OkDomain.setDomain(DOMAIN_API, if (type == DOMAIN_RELEASE) DOMAIN_URL_API else if (type == DOMAIN_TEST) DOMAIN_URL_API_TEST else DOMAIN_URL_API_BETA)
            OkDomain.setDomain(DOMAIN_H5, if (type == DOMAIN_RELEASE) DOMAIN_URL_H5 else if (type == DOMAIN_TEST) DOMAIN_URL_H5_TEST else DOMAIN_URL_H5_BETA)
            OkDomain.setDomain(DOMAIN_MQTT, if (type == DOMAIN_RELEASE) DOMAIN_MQTT_URL else DOMAIN_MQTT_URL_TEST)
        }

        fun getBaseUrl(): String {
            return if (BuildConfig.DEBUG) DOMAIN_URL_API_TEST else DOMAIN_URL_API
        }

        fun getWebDomainNameUrl(path: String = ""): String {
            return when(currentDoMain) {
                DOMAIN_RELEASE -> DOMAIN_URL_H5
                DOMAIN_BETA -> DOMAIN_URL_H5_BETA
                else -> DOMAIN_URL_H5_TEST
            } + path
        }
    }



    /************************************************************************************************************/
    /**
     * 登录
     */
    @GET("/openrollmachine/attendancelogin")
    suspend fun login(@Path("machineid") deviceId: String, @Path("username") username: String,
                      @Path("password") password: String, @Path("logintype") logintype: String = "1",
                      @Path("appver") appver: String): LoginResponse

    /**mqtt扫码打卡*/
    @Headers(OkDomain.DOMAIN_NAME_HEADER + DOMAIN_MQTT)
    @POST("turnstile/open")
    suspend fun mqttCard(@Body requestBody: RequestBody)


    suspend fun heartBeat()

    @GET("/uploads/getToken")
    suspend fun getQiniuToken(): QiniuResponse
}