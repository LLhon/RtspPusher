package com.ancda.rtsppusher.ui.adapter

import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author  : LLhon
 * date    : 2024/4/24 10:28.
 * des     :
 */
class SearchIpcAdapter : BaseQuickAdapter<Device, BaseViewHolder> (R.layout.item_search_ipc, null) {

    private var mSelPosition = 0

    fun setSelPosition(position: Int) {
        mSelPosition = position
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: Device) {
        if (holder.layoutPosition % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.colorPrimary)
        } else {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryVariant)
        }
        if (holder.layoutPosition == mSelPosition) {
            holder.itemView.setBackgroundResource(R.color.FF6C80EB)
        }
        holder.setText(R.id.tvChannelNo, "${holder.layoutPosition + 1}")
        holder.setText(R.id.tvIp, item.ipAddress)
        holder.setText(R.id.tvProtocol, "ONVIF(未知品牌)")
        holder.setText(R.id.tvPort, item.port)
        holder.setText(R.id.tvSerialNo, item.serialNumber)
    }
}