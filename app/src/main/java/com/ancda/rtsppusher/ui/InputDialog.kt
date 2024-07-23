package com.ancda.rtsppusher.ui

import android.content.Context
import android.text.InputFilter
import android.view.View
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogInputCameraBinding
import razerdp.basepopup.BasePopupWindow

class InputDialog(context: Context, private val onPositive: ((account: String, password: String) -> Unit)? = null): BasePopupWindow(context) {

    private lateinit var mBinding: DialogInputCameraBinding

    init {
        setContentView(R.layout.dialog_input_camera)
//        setBackPressEnable(false)
//        setOutSideDismiss(false)
//        isOutSideTouchable = false
    }

    override fun onViewCreated(contentView: View) {
        mBinding = DialogInputCameraBinding.bind(contentView).apply {
            btn.setOnClickListener {
                val account = etRtspUsername.text.toString().trim()
                val password = etRtspPassword.text.toString().trim()
                onPositive?.invoke(account, password)
                dismiss()
            }
        }
    }
}