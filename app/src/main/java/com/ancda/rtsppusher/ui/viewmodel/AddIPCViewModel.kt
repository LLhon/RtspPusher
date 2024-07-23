package com.ancda.rtsppusher.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.onvif.FindDevicesThread

/**
 * author  : LLhon
 * date    : 2024/3/25 9:47.
 * des     :
 */
class AddIPCViewModel : BaseViewModel() {

    val mSearchDeviceData = MutableLiveData<MutableList<Device>>()

    fun initRtspCamera() {
        // 启动摄像头相关的线程
//        RtspCameraHelper.sharedInstance().init()
    }

    /**
     * 搜索同一网段下的摄像头，可以是广播地址
     */
    fun searchIPC() {
        //搜索ipc摄像头
//        RtspCameraHelper.sharedInstance().searchIPCamera(mOnSearchCameraCallback)
        FindDevicesThread(MyApp.sInstance) {
            mSearchDeviceData.postValue(it)
        }.start()
    }

//    private val mOnSearchCameraCallback = object : OnSearchCameraCallback {
//        override fun onSearchCamera(ipList: MutableList<String>?) {
//            mSearchResultData.postValue(ipList)
//        }
//    }
}