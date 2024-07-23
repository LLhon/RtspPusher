package com.ancda.rtsppusher.http

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.net.ParseException
import com.ancda.base.ext.util.loge
import com.ancda.base.network.ApiResponseError
import com.ancda.base.network.AppException
import com.ancda.base.network.Error
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.hjq.gson.factory.GsonFactory
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * 描述　: 根据异常返回相关的错误信息工具类
 */

@SuppressLint("StaticFieldLeak")
object ExceptionHandle {

    fun handleException(e: Throwable?): AppException {
        when (e) {
            is HttpException -> {
                return try { //处理400、500错误，接收body
                    val errorJson: String? = e.response()?.errorBody()?.string()
                    errorJson?.loge("HttpLog")
                    val error = GsonFactory.getSingletonGson().fromJson(errorJson, ApiResponseError::class.java)
                    AppException(error, e)
                } catch (err: Exception) {
                    err.printStackTrace()
                    AppException(Error.NETWORK_ERROR,e)
                }
            }
            is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> {
                return AppException(Error.PARSE_ERROR,e)
            }
            is ConnectException -> {
                return AppException(Error.NETWORK_ERROR,e)
            }
            is javax.net.ssl.SSLException -> {
                return AppException(Error.SSL_ERROR,e)
            }
            is ConnectTimeoutException -> {
                return AppException(Error.TIMEOUT_ERROR,e)
            }
            is java.net.SocketTimeoutException -> {
                return AppException(Error.TIMEOUT_ERROR,e)
            }
            is java.net.UnknownHostException -> {
                return AppException(Error.TIMEOUT_ERROR,e)
            }
            is AppException -> return e

            else -> {
                return AppException(Error.UNKNOWN,e)
            }
        }
    }
}