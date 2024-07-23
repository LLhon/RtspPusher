package com.ancda.rtsppusher.view

import android.content.Context
import android.view.View
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogWarningBinding
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/4/26 17:28.
 * des     :
 */
class WarningDialog(context: Context) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.dialog_warning)
        setOutSideDismiss(false)
    }

    override fun onViewCreated(contentView: View) {
        DialogWarningBinding.bind(contentView).apply {
            tvCancel.setOnClickListener { dismiss() }
        }
    }
}