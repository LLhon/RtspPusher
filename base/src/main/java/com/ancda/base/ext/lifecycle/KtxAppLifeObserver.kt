package com.ancda.base.ext.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ancda.base.callback.livedata.BooleanLiveData

/**
 * 作者　: luoxx
 * 时间　: 20120/1/7
 * 描述　:
 */
object KtxAppLifeObserver : LifecycleEventObserver {

    var isForeground = BooleanLiveData()

    //在前台
    /*@OnLifecycleEvent(Lifecycle.Event.ON_START)
    private  fun onForeground() {
        isForeground.value = true
    }

    //在后台
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onBackground() {
        isForeground.value = false
    }*/

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_START) {
            isForeground.value = true
        } else if (event == Lifecycle.Event.ON_STOP) {
            isForeground.value = false
        }
    }

}