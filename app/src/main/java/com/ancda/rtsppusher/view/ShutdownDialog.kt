package com.ancda.rtsppusher.view

import android.content.Context
import android.view.View
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogShutdownBinding
import com.ancda.rtsppusher.utils.AdwApiHelper
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/6/22 16:12.
 * des     :
 */
class ShutdownDialog(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.dialog_shutdown)
        setOutSideDismiss(true)
    }

    override fun onViewCreated(contentView: View) {
        DialogShutdownBinding.bind(contentView).apply {
            ivCancel.setOnClickListener { dismiss() }
            llShutdown.setOnClickListener {
                //关机
                AdwApiHelper.shutdown()
            }
            llReboot.setOnClickListener {
                //重启
                AdwApiHelper.reboot()
            }
        }
    }
}