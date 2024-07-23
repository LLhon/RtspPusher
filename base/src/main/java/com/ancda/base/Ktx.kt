package com.ancda.base

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.ProcessLifecycleOwner
import com.ancda.base.ext.lifecycle.KtxAppLifeObserver
import com.ancda.base.ext.lifecycle.KtxLifeCycleCallBack
import com.ancda.base.network.manager.NetState
import com.ancda.base.network.manager.NetworkStateManager
import com.ancda.base.network.manager.NetworkStateReceive
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener
import com.blankj.utilcode.util.ProcessUtils

/**
 * 作者　: luoxx
 * 时间　: 2019/12/14
 * 描述　:
 */

val appContext: Application by lazy { Ktx.app }

class Ktx : ContentProvider() {

    companion object {
        lateinit var app: Application
        private var mNetworkStateReceive: NetworkStateReceive? = null
        var watchActivityLife = true
        var watchAppLife = true
    }

    override fun onCreate(): Boolean {
        if (ProcessUtils.isMainProcess()) {
            val application = context!!.applicationContext as Application
            install(application)
        }
        return true
    }

    private fun install(application: Application) {
        app = application
        NetworkUtils.registerNetworkStatusChangedListener(object : OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                NetworkStateManager.instance.mNetworkStateCallback.value = NetState(isSuccess = false)
            }

            override fun onConnected(networkType: NetworkUtils.NetworkType?) {
                NetworkStateManager.instance.mNetworkStateCallback.value = NetState(isSuccess = true)
            }
        })
//        mNetworkStateReceive = NetworkStateReceive()
//        app.registerReceiver(
//            mNetworkStateReceive,
//            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        )

        if (watchActivityLife) application.registerActivityLifecycleCallbacks(KtxLifeCycleCallBack())
        if (watchAppLife) ProcessLifecycleOwner.get().lifecycle.addObserver(KtxAppLifeObserver)
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}