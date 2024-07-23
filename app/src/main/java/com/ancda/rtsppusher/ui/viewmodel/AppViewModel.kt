package com.ancda.rtsppusher.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ancda.base.viewmodel.BaseViewModel
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.data.bean.UserBean

/**
 * author  : LLhon
 * date    : 2024/4/24 16:04.
 * des     :
 */
class AppViewModel : BaseViewModel() {

    var mIpcListData = MutableLiveData<MutableList<Device>>()
    var mUserListData = MutableLiveData<MutableList<UserBean>>()
}