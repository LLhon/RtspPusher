package com.ancda.rtsppusher.ui.viewmodel

import android.util.Log
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.appViewModel
import com.ancda.rtsppusher.constant.MmkvConstant
import com.ancda.rtsppusher.constant.getMmkvList
import com.ancda.rtsppusher.constant.mmkvUtils
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.data.bean.UserBean
import com.ancda.rtsppusher.utils.AdwApiHelper
import com.blankj.utilcode.util.TimeUtils
import java.util.Date

/**
 * author  : LLhon
 * date    : 2024/3/25 9:32.
 * des     :
 */
class MainViewModel : BaseViewModel() {

    fun readIpcList() {
        val ipcList = getMmkvList<Device>(MmkvConstant.IPC_LIST)
        Log.d("MainViewModel", "已添加的ipc列表=$ipcList")
        if (ipcList != null) {
            appViewModel.mIpcListData.value = ipcList
        }

        var userList = getMmkvList<UserBean>(MmkvConstant.USER_LIST)
        if (userList.isNullOrEmpty()) {
            userList = arrayListOf()
            userList.add(UserBean(1, "admin", "admin", "管理员"))
        }
        appViewModel.mUserListData.value = userList
    }

    /**
     * 初始化配置
     */
    fun initConfig() {
        val sleepDurationList = MyApp.sInstance.resources.getStringArray(R.array.ScreenSleepDuration)
        val screenSleepIndex = mmkvUtils().getInt(MmkvConstant.SCREEN_SLEEP_INDEX, 3)
        MyApp.sSleepDuration = when (sleepDurationList[screenSleepIndex]) {
            "30秒" -> 30
            "1分钟" -> 60
            "3分钟" -> 180
            "5分钟" -> 300
            "10分钟" -> 600
            else -> 300
        }

        //设置定时开关机：凌晨3点自动关机，3点半开机
        val date = TimeUtils.date2String(Date(), "yyyy-MM-dd")
        AdwApiHelper.setPowerOffOnTime("${date}-03-00-00", "${date}-03-30-00", true)
    }
}