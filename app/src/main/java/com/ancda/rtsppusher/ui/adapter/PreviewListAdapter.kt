package com.ancda.rtsppusher.ui.adapter

import android.view.View
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.ui.IPCameraInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author  : LLhon
 * date    : 2024/3/26 10:14.
 * des     :
 */
class PreviewListAdapter : BaseQuickAdapter<IPCameraInfo, PreviewListViewHolder>(R.layout.item_preview_list, null) {

    override fun convert(holder: PreviewListViewHolder, item: IPCameraInfo) {

    }
}

class PreviewListViewHolder(view: View) : BaseViewHolder(view) {


}