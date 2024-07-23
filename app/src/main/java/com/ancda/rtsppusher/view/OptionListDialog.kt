package com.ancda.rtsppusher.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancda.rtsppusher.R
import com.ancda.rtsppusher.databinding.DialogOptionListBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import razerdp.basepopup.BasePopupWindow

/**
 * author  : LLhon
 * date    : 2024/4/25 14:43.
 * des     :
 */
class OptionListDialog(private val context: Context, private var data: MutableList<String>, private val selPosition: Int = 0, private var listener: OnOptionClickListener? = null) : BasePopupWindow(context) {

    init {
        setContentView(R.layout.dialog_option_list)
    }

    override fun onViewCreated(contentView: View) {
        DialogOptionListBinding.bind(contentView).apply {

            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter = OptionListAdapter()
            adapter.setList(data)
            rv.adapter = adapter
            adapter.setOnItemClickListener { _, _, position ->
                dismiss()
                listener?.onOptionClick(position)
            }
        }
    }

    inner class OptionListAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_option) {

        override fun convert(holder: BaseViewHolder, item: String) {
            if (holder.layoutPosition == selPosition) {
                holder.itemView.setBackgroundResource(R.color.FF6C80EB)
            } else {
                holder.itemView.setBackgroundResource(android.R.color.transparent)
            }
            holder.setText(R.id.tv, item)
        }
    }

    interface OnOptionClickListener {
        fun onOptionClick(position: Int)
    }
}