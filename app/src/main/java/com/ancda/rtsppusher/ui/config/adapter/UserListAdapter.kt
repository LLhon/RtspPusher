package com.ancda.rtsppusher.ui.config.adapter

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.data.bean.Device
import com.ancda.rtsppusher.data.bean.UserBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * author  : LLhon
 * date    : 2024/4/23 15:43.
 * des     :
 */
class UserListAdapter : BaseQuickAdapter<UserBean, BaseViewHolder>(R.layout.item_user_list, null) {

    override fun convert(holder: BaseViewHolder, item: UserBean) {
        if (holder.layoutPosition % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.colorPrimary)
        } else {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryVariant)
        }
        holder.setText(R.id.tvNo, "${item.id}")
        holder.setText(R.id.tvUserName, item.userName)
        holder.setText(R.id.tvLevel, item.level)
    }
}