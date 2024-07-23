package com.ancda.rtsppusher.ui.config.adapter

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * author  : LLhon
 * date    : 2024/4/23 15:43.
 * des     :
 */
class ChannelListAdapter : BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_channel_list, null) {

    private var tooltip: Toast? = null

    override fun convert(holder: BaseViewHolder, item: Device) {
        if (holder.layoutPosition % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.colorPrimary)
        } else {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryVariant)
        }
        if (item.status == 1) {
            holder.setImageResource(R.id.ivStatus, R.drawable.ic_online)
        } else {
            holder.setImageResource(R.id.ivStatus, R.drawable.ic_offline)
        }
        holder.setText(R.id.tvChannelNo, "D${item.channelNo}")
        holder.setText(R.id.tvIp, item.ipAddress)
        holder.setText(R.id.tvChannelName, item.manufacturer)
        holder.setText(R.id.tvProtocolName, "ONVIF(未知设备)")
        holder.setText(R.id.tvDeviceType, item.modle)
        holder.setText(R.id.tvSerialNo, item.serialNumber)
        holder.setText(R.id.tvPort, item.port)
        holder.setText(R.id.tvFirmware, item.firmwareVersion)

        //监听鼠标悬停事件
        val ivStatus = holder.getView<ImageView>(R.id.ivStatus)
        ivStatus.setOnHoverListener { view, motionEvent ->
            if (item.status == 1) {
                return@setOnHoverListener true
            }
            when (motionEvent.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    // 鼠标进入视图，显示悬浮提示
                    showToolTip(view, item.connectErrorMsg)
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    // 鼠标离开视图，隐藏悬浮提示
                    hideTooltip()
                }
            }
            return@setOnHoverListener true
        }
    }

    private fun showToolTip(anchorView: View, message: String) {
        hideTooltip() // 首先隐藏现有的悬浮提示

        tooltip = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        tooltip?.setGravity(
            Gravity.TOP or Gravity.START,
            anchorView.x.toInt(),
            anchorView.y.toInt() + anchorView.height
        )
        tooltip?.show()
    }

    private fun hideTooltip() {
        tooltip?.cancel()
        tooltip = null
    }
}