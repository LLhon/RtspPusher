package com.ancda.rtsppusher.ui.viewmodel

import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.ext.request
import com.ancda.rtsppusher.http.apiService
import com.blankj.utilcode.util.ToastUtils

/**
 * author  : LLhon
 * date    : 2024/6/25 16:06.
 * des     :
 */
class SplashViewModel : BaseViewModel() {

    fun login(onError: () -> Unit) {
        request({ apiService.login(MyApp.deviceID, "", "", "1", "")}, success = {

        }, error = {
            ToastUtils.showShort(it.errorMsg)
            onError.invoke()
        }, false)
    }
}