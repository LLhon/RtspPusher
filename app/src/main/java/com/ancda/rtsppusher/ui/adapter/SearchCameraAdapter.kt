package com.ancda.rtsppusher.ui.adapter

import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author  : LLhon
 * date    : 2024/3/25 10:38.
 * des     :
 */
class SearchCameraAdapter : BaseQuickAdapter<Device, BaseViewHolder>(R.layout.item_search_camera, null) {

    override fun convert(holder: BaseViewHolder, item: Device) {
        holder.setText(R.id.tvIp, item.ipAddress)
        holder.setText(R.id.tvManufacturer, item.manufacturer)
        holder.setText(R.id.tvAdd, if (item.status == 0) "添加" else "连接成功")
    }
}