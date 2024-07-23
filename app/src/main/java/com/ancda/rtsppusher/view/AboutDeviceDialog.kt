package com.ancda.rtsppusher.view

import android.content.Context
import android.view.View
import com.ancda.base.ext.util.dp2px
import com.ancda.rtsppusher.BuildConfig
import com.ancda.rtsppusher.MyApp
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogDeviceInfoBinding
import com.ancda.rtsppusher.ext.dp
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.ToastUtils
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/6/24 10:59.
 * des     :
 */
class AboutDeviceDialog(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.dialog_device_info)
        setOutSideDismiss(true)
    }

    override fun onViewCreated(contentView: View) {
        DialogDeviceInfoBinding.bind(contentView).apply {
            tvSN.text = "序列号:${MyApp.deviceID}"
            tvVersion.text = "版本号:V${BuildConfig.VERSION_NAME}"
            tvFirmware.text = "固件版本:"

            tvReset.setOnClickListener {
                //简单恢复
                ToastUtils.showShort("恢复成功")
            }
            tvRecovery.setOnClickListener {
                //恢复出厂设置
                // TODO:
                CacheDiskUtils.getInstance().clear()
                ToastUtils.showShort("出厂设置成功!")
            }
        }
    }
}