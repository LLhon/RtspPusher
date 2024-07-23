package com.ancda.rtsppusher.http

import com.ancda.base.appContext
import com.ancda.base.network.BaseNetworkApi
import com.ancda.base.network.interceptor.CacheInterceptor
import com.ancda.rtsppusher.http.interceptor.CommonInterceptor
import com.ancda.rtsppusher.http.interceptor.HttpLoggingInterceptor2
import com.ancda.rtsppusher.utils.log.ALog
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.hjq.gson.factory.GsonFactory
import libcore.net.okhttp.addOkDomain
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Description: 网络请求工具
 */

//双重校验锁式-单例 封装NetApiService 方便直接快速调用简单的接口
val apiService: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    NetworkApi.INSTANCE.getApi(ApiService::class.java, ApiService.getBaseUrl())
}

class NetworkApi : BaseNetworkApi() {

    companion object {
        val INSTANCE: NetworkApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkApi()
        }
    }

    private val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))
    }

    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.apply {
            sslSocketFactory(SSLSocketClient.sSLSocketFactory, SSLSocketClient.IGNORE_SSL_TRUST_MANAGER_X509)
            hostnameVerifier(SSLSocketClient.hostnameVerifier)
            addOkDomain(ApiService.getBaseUrl())
            //设置缓存配置 缓存最大10M
            cache(Cache(File(appContext.cacheDir, "cxk_cache"), 10 * 1024 * 1024))
            //添加Cookies自动持久化
            cookieJar(cookieJar)
            //示例：添加公共heads 注意要设置在日志拦截器之前，不然Log中会不显示head信息
            addInterceptor(CommonInterceptor())
            //添加缓存拦截器 可传入缓存天数，不传默认7天
            addInterceptor(CacheInterceptor())
            // 日志拦截器
//            addInterceptor(LogInterceptor())
            addInterceptor(HttpLoggingInterceptor2(HttpLoggingInterceptor2.Level.BODY) {
                ALog.dToFile("HttpLog", it)
            })

            //超时时间 连接、读、写
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
        }
        //添加一个域名
        ApiService.setDomain(ApiService.currentDoMain)
        return builder
    }

    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
//            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            //gson容错
            addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        }
    }
}