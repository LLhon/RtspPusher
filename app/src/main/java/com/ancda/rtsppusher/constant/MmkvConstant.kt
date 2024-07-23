package com.ancda.rtsppusher.constant

import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.hjq.gson.factory.GsonFactory
import com.tencent.mmkv.MMKV

/**
 * author  : LLhon
 * date    : 2024/4/28 9:46.
 * des     :
 */
object MmkvConstant {

    const val IPC_LIST = "ipcList"
    const val USER_LIST = "userList"
    const val SCREEN_SLEEP_INDEX = "screenSleepIndex"
    //是否开启操作密码
    const val ENABLE_OPERATION_PWD = "enableOperationPwd"
    //是否恢复推流
    const val IS_RESUME_PUSH_STREAM = "isResumePushStream"
}

fun mmkvUtils(): MMKV = MMKV.defaultMMKV()

fun getMmkvString(key: String?): String =  mmkvUtils().getString(key, "")?:""

fun putMmkvString(key: String?, value: String? = ""): SharedPreferences.Editor =  mmkvUtils().putString(key, value)

fun getMmkvBoolean(key: String?, defValue: Boolean = false): Boolean =  mmkvUtils().getBoolean(key, defValue)

fun putMmkvBoolean(key: String?, value: Boolean): SharedPreferences.Editor =  mmkvUtils().putBoolean(key, value)

fun getMmkvInt(key: String?, defValue: Int = 0): Int =  mmkvUtils().getInt(key, defValue)

fun putMmkvInt(key: String?, value: Int): SharedPreferences.Editor =  mmkvUtils().putInt(key, value)

fun getMmkvLong(key: String?, defValue: Long = 0): Long =  mmkvUtils().getLong(key, defValue)

fun putMmkvLong(key: String?, value: Long): SharedPreferences.Editor =  mmkvUtils().putLong(key, value)

fun getMmkvFloat(key: String?, defValue: Float = 0F): Float =  mmkvUtils().getFloat(key, defValue)

fun putMmkvFloat(key: String?, value: Float): SharedPreferences.Editor =  mmkvUtils().putFloat(key, value)

fun getMmkvStringSet(key: String?, defValue: Set<String>? = setOf()): Set<String> =  mmkvUtils().getStringSet(key, defValue)?: setOf()

fun putMmkvStringSet(key: String?, value: Set<String>?): SharedPreferences.Editor =  mmkvUtils().putStringSet(key, value)

fun <T: Parcelable> getMmkvParcelable(key: String?, tClass: Class<T>): T?  = mmkvUtils().decodeParcelable(key, tClass)

fun putMmkvParcelable(key: String?, value: Parcelable?): Boolean = mmkvUtils().encode(key, value)

/**mmkv 取出object对象*/
fun <T> getMmkvObject(key: String?, tClass: Class<T>): T? {
    val value: String? = mmkvUtils().decodeString(key, null)
    if (value?.isNotEmpty() == true) {
        return GsonFactory.getSingletonGson().fromJson(value, tClass)
    }
    return null
}

/**mmkv 取出object对象*/
inline fun <reified T> getMmkvObject(key: String?): T? {
    val json = mmkvUtils().decodeString(key)
    return GsonFactory.getSingletonGson().fromJson(json, T::class.java)
}

/**取出List对象*/
inline fun <reified T> getMmkvList(key: String?): ArrayList<T>? {
    val json = mmkvUtils().decodeString(key) ?: return null
    try {
        val element = JsonParser.parseString(json)
        if (element.isJsonArray) {
            val list = arrayListOf<T>()
            for (jsonElement in element.asJsonArray) {
                list.add(GsonFactory.getSingletonGson().fromJson(jsonElement, T::class.java))
            }
            return list
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**可放入object、array*/
fun putMmkvObject(key: String?, value: Any?): Boolean = mmkvUtils().encode(key, Gson().toJson(value))