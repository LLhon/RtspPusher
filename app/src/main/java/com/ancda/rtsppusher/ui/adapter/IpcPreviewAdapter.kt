package com.ancda.rtsppusher.ui.adapter

import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author  : LLhon
 * date    : 2024/4/22 17:51.
 * des     :
 */
class IpcPreviewAdapter : BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_ipc_preview, null) {

    var mSelIndex = 0

    override fun convert(holder: BaseViewHolder, item: Device) {
        holder.setText(R.id.tvName, "[D${item.channelNo}] ${item.manufacturer}")
        if (item.status == 1) {
            //摄像头在线
            if (mSelIndex == holder.layoutPosition) {
                holder.itemView.setBackgroundResource(R.color.colorPrimary)
            } else {
                holder.itemView.setBackgroundResource(R.color.colorPrimaryVariant)
            }
            holder.setTextColorRes(R.id.tvName, R.color.white)
        } else {
            //摄像头离线
            holder.itemView.setBackgroundResource(R.color.colorPrimaryVariant)
            holder.setTextColorRes(R.id.tvName, R.color.divider_line)
        }
    }
}