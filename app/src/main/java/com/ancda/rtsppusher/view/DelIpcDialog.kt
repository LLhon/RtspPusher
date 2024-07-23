package com.ancda.rtsppusher.view

import android.content.Context
import android.view.View
import com.ancda.base.appContext
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogDelIpcBinding
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/4/25 11:43.
 * des     :
 */
class DelIpcDialog(private val context: Context, private var listener: OnDelIpcListener? = null) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.dialog_del_ipc)
        setOutSideDismiss(false)
    }

    override fun onViewCreated(contentView: View) {
        DialogDelIpcBinding.bind(contentView).apply {

            tvCancel.setOnClickListener { dismiss() }

            tvOk.setOnClickListener {
                dismiss()

                listener?.onDelIpc()
            }
        }
    }

    interface OnDelIpcListener {
        fun onDelIpc()
    }
}